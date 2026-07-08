package com.acme.assistant.chapter12.services;

import com.acme.assistant.chapter12.agents.CoordinatorAgentService;
import com.acme.assistant.chapter12.agents.SpecialistAgentService;
import com.acme.assistant.chapter12.audit.AuditService;
import com.acme.assistant.chapter12.audit.SecurityEventService;
import com.acme.assistant.chapter12.dto.ChatRequest;
import com.acme.assistant.chapter12.dto.ChatResponse;
import com.acme.assistant.chapter12.dto.DocumentResponse;
import com.acme.assistant.chapter12.dto.ApprovalResponse;
import com.acme.assistant.chapter12.governance.GovernancePolicyService;
import com.acme.assistant.chapter12.memory.ConversationMemoryService;
import com.acme.assistant.chapter12.rag.RagSecurityService;
import com.acme.assistant.chapter12.security.AuthorizationService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatOrchestrationService {

    private final PromptSecurityService promptSecurityService;
    private final AuthorizationService authorizationService;
    private final GovernancePolicyService governancePolicyService;
    private final ApprovalService approvalService;
    private final ConversationMemoryService conversationMemoryService;
    private final RagSecurityService ragSecurityService;
    private final CoordinatorAgentService coordinatorAgentService;
    private final SpecialistAgentService specialistAgentService;
    private final AuditService auditService;
    private final SecurityEventService securityEventService;

    public ChatOrchestrationService(
            PromptSecurityService promptSecurityService,
            AuthorizationService authorizationService,
            GovernancePolicyService governancePolicyService,
            ApprovalService approvalService,
            ConversationMemoryService conversationMemoryService,
            RagSecurityService ragSecurityService,
            CoordinatorAgentService coordinatorAgentService,
            SpecialistAgentService specialistAgentService,
            AuditService auditService,
            SecurityEventService securityEventService) {
        this.promptSecurityService = promptSecurityService;
        this.authorizationService = authorizationService;
        this.governancePolicyService = governancePolicyService;
        this.approvalService = approvalService;
        this.conversationMemoryService = conversationMemoryService;
        this.ragSecurityService = ragSecurityService;
        this.coordinatorAgentService = coordinatorAgentService;
        this.specialistAgentService = specialistAgentService;
        this.auditService = auditService;
        this.securityEventService = securityEventService;
    }

    @Transactional
    public ChatResponse startConversation(ChatRequest request) {
        return continueConversation("CONV-" + UUID.randomUUID(), request);
    }

    @Transactional
    public ChatResponse continueConversation(String conversationId, ChatRequest request) {
        authorizationService.assertSameUserOrAdmin(request.userId());
        PromptSecurityService.PromptSecurityResult promptSecurity = promptSecurityService.validatePrompt(request.message());

        if (promptSecurity.blocked()) {
            securityEventService.record(
                    authorizationService.currentUser(),
                    "PROMPT_BLOCKED",
                    "HIGH",
                    String.join("; ", promptSecurity.findings()));
            auditService.record(
                    authorizationService.currentUser(),
                    conversationId,
                    "N/A",
                    "CHAT_REQUEST",
                    "DENY",
                    promptSecurity.riskScore(),
                    "Prompt blocked");
            return new ChatResponse(
                    conversationId,
                    "Your request was blocked by prompt security controls.",
                    "NONE",
                    promptSecurity.riskScore(),
                    false,
                    null);
        }

        GovernancePolicyService.GovernanceDecision governance = governancePolicyService.evaluate(request.message());
        if (governance.requiresApproval()) {
            ApprovalResponse approval = approvalService.createApproval(
                    governance.operation(),
                    request.message(),
                    governance.requiredRole(),
                    "{\"conversationId\":\"" + conversationId + "\"}");
            securityEventService.record(
                    authorizationService.currentUser(),
                    "HITL_REQUIRED",
                    "MEDIUM",
                    governance.reason());
            auditService.record(
                    authorizationService.currentUser(),
                    conversationId,
                    "N/A",
                    "GOVERNANCE",
                    "DENY",
                    promptSecurity.riskScore(),
                    governance.reason());
            return new ChatResponse(
                    conversationId,
                    "Human approval is required before this action can continue.",
                    "NONE",
                    promptSecurity.riskScore(),
                    true,
                    approval.id());
        }

        conversationMemoryService.appendMessage(conversationId, request.userId(), "user", request.message());
        List<String> context = conversationMemoryService.recentContext(conversationId);
        List<String> ragChunks = ragSecurityService.retrieveSanitizedDocumentChunksForUser(request.message());
        String selectedAgent = coordinatorAgentService.selectAgent(request.message());

        String response = specialistAgentService.execute(
                authorizationService.currentUser(),
                conversationId,
                selectedAgent,
                request.message(),
                context,
                ragChunks);

        conversationMemoryService.appendMessage(conversationId, request.userId(), "assistant", response);

        auditService.record(
                authorizationService.currentUser(),
                conversationId,
                selectedAgent,
                "CHAT_REQUEST",
                "ALLOW",
                promptSecurity.riskScore(),
                "retrievedDocuments=" + ragChunks.size());

        List<DocumentResponse> docs = ragSecurityService.listAuthorizedDocuments(null);
        securityEventService.record(
                authorizationService.currentUser(),
                "RAG_ACCESS",
                "LOW",
                "authorizedDocuments=" + docs.size());

        return new ChatResponse(
                conversationId,
                response,
                selectedAgent,
                promptSecurity.riskScore(),
                false,
                null);
    }
}
