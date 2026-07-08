package com.acme.assistant.chapter12.dto;

import java.time.Instant;

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
