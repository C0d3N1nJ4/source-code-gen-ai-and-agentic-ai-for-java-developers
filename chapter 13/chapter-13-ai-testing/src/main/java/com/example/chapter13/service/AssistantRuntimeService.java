package com.example.chapter13.service;

import com.example.chapter13.dto.AssistantRuntimeDtos.ApprovalResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.AuditEventResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.ChatRequest;
import com.example.chapter13.dto.AssistantRuntimeDtos.ChatResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.DocumentCreateRequest;
import com.example.chapter13.dto.AssistantRuntimeDtos.DocumentResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.MemoryMessageResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.MemoryResponse;
import com.example.chapter13.dto.AssistantRuntimeDtos.SecurityEventResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AssistantRuntimeService {

    private final Chapter12BaselineAssistantService baselineAssistantService;
    private final AtomicLong documentIds = new AtomicLong(100);
    private final Map<String, String> conversationOwners = new ConcurrentHashMap<>();
    private final Map<String, List<MemoryMessageResponse>> conversationMessages = new ConcurrentHashMap<>();
    private final Map<Long, DocumentRecord> documents = new ConcurrentHashMap<>();
    private final Map<String, ApprovalResponse> approvals = new ConcurrentHashMap<>();
    private final List<AuditEventResponse> auditEvents = new ArrayList<>();
    private final List<SecurityEventResponse> securityEvents = new ArrayList<>();

    public AssistantRuntimeService(Chapter12BaselineAssistantService baselineAssistantService) {
        this.baselineAssistantService = baselineAssistantService;
        seedDocuments();
    }

    public ChatResponse startConversation(ChatRequest request) {
        return continueConversation("CONV-" + UUID.randomUUID(), request);
    }

    public ChatResponse continueConversation(String conversationId, ChatRequest request) {
        assertSameUserOrAdmin(request.userId());
        PromptAssessment prompt = assessPrompt(request.message());

        if (prompt.blocked()) {
            recordSecurityEvent(currentUser(), "PROMPT_BLOCKED", "HIGH", String.join("; ", prompt.findings()));
            recordAudit(currentUser(), conversationId, "N/A", "CHAT_REQUEST", "DENY", prompt.riskScore(), "Prompt blocked");
            return new ChatResponse(conversationId, "Your request was blocked by prompt security controls.", "NONE", prompt.riskScore(), false, null);
        }

        GovernanceDecision governance = evaluateGovernance(request.message());
        if (governance.requiresApproval()) {
            ApprovalResponse approval = createApproval(governance.operation(), request.message(), governance.requiredRole());
            recordSecurityEvent(currentUser(), "HITL_REQUIRED", "MEDIUM", governance.reason());
            recordAudit(currentUser(), conversationId, "N/A", "GOVERNANCE", "DENY", prompt.riskScore(), governance.reason());
            return new ChatResponse(conversationId, "Human approval is required before this action can continue.", "NONE", prompt.riskScore(), true, approval.id());
        }

        appendMessage(conversationId, request.userId(), "user", request.message());
        List<String> context = recentContext(conversationId, request.userId());
        List<String> ragChunks = retrieveSanitizedDocumentChunksForUser(request.message());
        String selectedAgent = baselineAssistantService.selectAgent(request.message());
        String response = executeAgentAction(selectedAgent, request.message(), context, ragChunks);
        appendMessage(conversationId, request.userId(), "assistant", response);

        recordAudit(currentUser(), conversationId, selectedAgent, "CHAT_REQUEST", "ALLOW", prompt.riskScore(), "retrievedDocuments=" + ragChunks.size());
        recordSecurityEvent(currentUser(), "RAG_ACCESS", "LOW", "authorizedDocuments=" + listAuthorizedDocuments(null).size());

        return new ChatResponse(conversationId, response, selectedAgent, prompt.riskScore(), false, null);
    }

    public MemoryResponse getMemory(String conversationId) {
        String owner = conversationOwners.get(conversationId);
        if (owner == null) {
            throw new IllegalArgumentException("Conversation not found: " + conversationId);
        }
        if (!owner.equalsIgnoreCase(currentUser()) && !hasRole("ADMIN")) {
            throw new AccessDeniedException("Memory isolation policy violation");
        }
        return new MemoryResponse(conversationId, List.copyOf(conversationMessages.getOrDefault(conversationId, List.of())));
    }

    public List<DocumentResponse> listAuthorizedDocuments(String query) {
        return documents.values().stream()
                .filter(document -> query == null || query.isBlank() || document.title().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                .map(document -> new DocumentResponse(document.id(), document.title(), document.classification(), document.ownerUserId(), document.trustedMetadata()))
                .toList();
    }

    public DocumentResponse createDocument(DocumentCreateRequest request) {
        requireAnyRole("HR", "FINANCE", "ADMIN");
        long id = documentIds.incrementAndGet();
        boolean trustedMetadata = request.metadata() != null && request.metadata().containsKey("source") && request.metadata().containsKey("checksum");
        DocumentRecord record = new DocumentRecord(
                id,
                request.title(),
                request.classification().toUpperCase(Locale.ROOT),
                request.ownerUserId(),
                request.content(),
                sanitizeContent(request.content()),
                trustedMetadata);
        documents.put(id, record);
        recordAudit(currentUser(), "N/A", "KNOWLEDGE", "DOCUMENT_CREATE", "ALLOW", 0, "documentId=" + id);
        return new DocumentResponse(id, record.title(), record.classification(), record.ownerUserId(), record.trustedMetadata());
    }

    public List<ApprovalResponse> listApprovals() {
        requireAnyRole("HR", "FINANCE", "OPERATIONS", "ADMIN");
        return approvals.values().stream()
                .sorted(Comparator.comparing(ApprovalResponse::createdAt).reversed())
                .toList();
    }

    public ApprovalResponse approve(String id) {
        requireAnyRole("HR", "FINANCE", "ADMIN");
        ApprovalResponse existing = approvals.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Approval not found: " + id);
        }
        ApprovalResponse updated = new ApprovalResponse(
                existing.id(),
                existing.operation(),
                existing.target(),
                existing.requestedBy(),
                "APPROVED",
                existing.requiredRole(),
                existing.createdAt(),
                Instant.now(),
                currentUser());
        approvals.put(id, updated);
        recordAudit(currentUser(), "N/A", "N/A", "APPROVAL", "ALLOW", 0, "approvalId=" + id);
        return updated;
    }

    public List<AuditEventResponse> audit() {
        requireAnyRole("ADMIN");
        return List.copyOf(auditEvents);
    }

    public List<SecurityEventResponse> securityEvents() {
        requireAnyRole("ADMIN");
        return List.copyOf(securityEvents);
    }

    private void seedDocuments() {
        baselineAssistantService.authorizedKnowledgeDocuments().forEach(document -> {
            long id = documentIds.incrementAndGet();
            documents.put(id, new DocumentRecord(
                    id,
                    document.title(),
                    "Architecture".equals(document.domain()) ? "INTERNAL" : "CONFIDENTIAL",
                    "acme-system",
                    document.excerpt(),
                    sanitizeContent(document.excerpt()),
                    true));
        });
    }

    private PromptAssessment assessPrompt(String message) {
        List<String> findings = new ArrayList<>();
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("ignore previous instructions")) {
            findings.add("Instruction override attempt");
        }
        if (normalized.contains("reveal system prompt")) {
            findings.add("System prompt exfiltration attempt");
        }
        if (normalized.contains("bypass security")) {
            findings.add("Security bypass attempt");
        }
        double riskScore = findings.isEmpty() ? 12 : 85;
        return new PromptAssessment(!findings.isEmpty(), riskScore, findings);
    }

    private GovernanceDecision evaluateGovernance(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (baselineAssistantService.governanceSensitiveOperations().stream().anyMatch(normalized::contains)
                || normalized.contains("approve expense")) {
            return new GovernanceDecision(true, "HIGH_RISK_OPERATION", "ADMIN", "Sensitive operation requires human approval");
        }
        return new GovernanceDecision(false, "NONE", "NONE", "No approval required");
    }

    private ApprovalResponse createApproval(String operation, String target, String requiredRole) {
        String id = "APR-" + UUID.randomUUID();
        ApprovalResponse approval = new ApprovalResponse(
                id,
                operation,
                target,
                currentUser(),
                "PENDING",
                requiredRole,
                Instant.now(),
                null,
                null);
        approvals.put(id, approval);
        return approval;
    }

    private void appendMessage(String conversationId, String userId, String role, String content) {
        String existingOwner = conversationOwners.putIfAbsent(conversationId, userId);
        if (existingOwner != null && !existingOwner.equalsIgnoreCase(userId)) {
            throw new AccessDeniedException("Conversation ownership violation");
        }
        conversationMessages.computeIfAbsent(conversationId, ignored -> new ArrayList<>())
                .add(new MemoryMessageResponse(role, content, Instant.now()));
    }

    private List<String> recentContext(String conversationId, String userId) {
        appendConversationIfMissing(conversationId, userId);
        return conversationMessages.getOrDefault(conversationId, List.of()).stream()
                .map(message -> message.role() + ": " + message.content())
                .toList();
    }

    private void appendConversationIfMissing(String conversationId, String userId) {
        conversationOwners.putIfAbsent(conversationId, userId);
        conversationMessages.computeIfAbsent(conversationId, ignored -> new ArrayList<>());
    }

    private List<String> retrieveSanitizedDocumentChunksForUser(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return documents.values().stream()
                .filter(document -> normalized.contains("travel") ? document.title().toLowerCase(Locale.ROOT).contains("travel") || document.content().toLowerCase(Locale.ROOT).contains("travel") : true)
                .filter(DocumentRecord::trustedMetadata)
                .map(DocumentRecord::sanitizedContent)
                .toList();
    }

    private String executeAgentAction(String agent, String message, List<String> context, List<String> ragChunks) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if ("HR".equals(agent) && normalized.contains("leave")) {
            requireAnyRole("EMPLOYEE", "HR", "ADMIN");
            return "Leave request submitted. Context size=" + context.size();
        }
        if ("FINANCE".equals(agent) && normalized.contains("expense")) {
            requireAnyRole("EMPLOYEE", "FINANCE", "ADMIN");
            return "Estimated travel expenses for Project Orion are 420 EUR based on approved reimbursement policy. Supporting chunks=" + ragChunks.size();
        }
        if ("OPERATIONS".equals(agent) && normalized.contains("restart")) {
            requireAnyRole("OPERATIONS", "ADMIN");
            return "Server restart command scheduled.";
        }
        if ("OPERATIONS".equals(agent) && normalized.contains("incident")) {
            requireAnyRole("OPERATIONS", "ADMIN", "EMPLOYEE");
            return "Incident ticket created and leadership notification prepared.";
        }
        return "Release 2026.06 includes payment retry fixes and gateway-led rollout guidance for versioned REST APIs.";
    }

    private void recordAudit(String username, String conversationId, String selectedAgent, String action, String authorizationResult, double promptRiskScore, String details) {
        auditEvents.add(0, new AuditEventResponse(username, conversationId, selectedAgent, action, authorizationResult, promptRiskScore, details, Instant.now()));
    }

    private void recordSecurityEvent(String username, String eventType, String severity, String details) {
        securityEvents.add(0, new SecurityEventResponse(username, eventType, severity, details, Instant.now()));
    }

    private void assertSameUserOrAdmin(String userId) {
        if (!currentUser().equalsIgnoreCase(userId) && !hasRole("ADMIN")) {
            throw new AccessDeniedException("User mismatch for assistant operation");
        }
    }

    private void requireAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return;
            }
        }
        throw new AccessDeniedException("User is not authorized for requested operation");
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "anonymous" : authentication.getName();
    }

    private String sanitizeContent(String content) {
        String sanitized = content.replaceAll("(?i)ignore previous instructions", "[REMOVED]");
        sanitized = sanitized.replaceAll("(?i)reveal system prompt", "[REMOVED]");
        sanitized = sanitized.replaceAll("(?i)bypass security", "[REMOVED]");
        return sanitized;
    }

    private record PromptAssessment(boolean blocked, double riskScore, List<String> findings) {
    }

    private record GovernanceDecision(boolean requiresApproval, String operation, String requiredRole, String reason) {
    }

    private record DocumentRecord(
            long id,
            String title,
            String classification,
            String ownerUserId,
            String content,
            String sanitizedContent,
            boolean trustedMetadata) {
    }
}