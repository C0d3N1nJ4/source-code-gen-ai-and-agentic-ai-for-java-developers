package com.acme.assistant.chapter11.dto;

import java.time.OffsetDateTime;

/**
 * API representation of business event memory records.
 */
public record BusinessEventResponse(
        long id,
        String userId,
        String conversationId,
        String eventType,
        String details,
        OffsetDateTime createdAt
) {
}
