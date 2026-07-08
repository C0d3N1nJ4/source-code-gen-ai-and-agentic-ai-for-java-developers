package com.acme.assistant.chapter12.dto;

import java.time.Instant;

public record SecurityEventResponse(
        String username,
        String eventType,
        String severity,
        String details,
        Instant timestamp) {
}
