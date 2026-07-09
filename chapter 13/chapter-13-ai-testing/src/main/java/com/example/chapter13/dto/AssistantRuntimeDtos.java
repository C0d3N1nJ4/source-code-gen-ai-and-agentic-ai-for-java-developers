package com.example.chapter13.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class AssistantRuntimeDtos {

    private AssistantRuntimeDtos() {
    }

    public record ChatRequest(
            @NotBlank(message = "userId is required") String userId,
            @NotBlank(message = "message is required") String message) {
    }

    public record ChatResponse(
            String conversationId,
            String response,
            String selectedAgent,
            double promptRiskScore,
            boolean requiresApproval,
            String approvalRequestId) {
    }

    public record DocumentCreateRequest(
            @NotBlank String title,
            @NotBlank String classification,
            @NotBlank String ownerUserId,
            @NotBlank String content,
            Map<String, Object> metadata) {
    }

    public record DocumentResponse(
            Long id,
            String title,
            String classification,
            String ownerUserId,
            boolean trustedMetadata) {
    }

    public record MemoryResponse(String conversationId, List<MemoryMessageResponse> messages) {
    }

    public record MemoryMessageResponse(String role, String content, Instant createdAt) {
    }

    public record ApprovalDecisionRequest(String decision) {
    }

    public record ApprovalResponse(
            String id,
            String operation,
            String target,
            String requestedBy,
            String status,
            String requiredRole,
            Instant createdAt,
            Instant decidedAt,
            String decidedBy) {
    }

    public record AuditEventResponse(
            String username,
            String conversationId,
            String selectedAgent,
            String action,
            String authorizationResult,
            double promptRiskScore,
            String details,
            Instant timestamp) {
    }

    public record SecurityEventResponse(
            String username,
            String eventType,
            String severity,
            String details,
            Instant timestamp) {
    }
}