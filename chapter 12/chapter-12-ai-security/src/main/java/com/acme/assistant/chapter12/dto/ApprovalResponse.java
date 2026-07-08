package com.acme.assistant.chapter12.dto;

import java.time.Instant;

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
