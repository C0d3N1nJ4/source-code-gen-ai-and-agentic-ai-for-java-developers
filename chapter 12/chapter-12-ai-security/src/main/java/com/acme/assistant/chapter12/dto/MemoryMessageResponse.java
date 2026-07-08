package com.acme.assistant.chapter12.dto;

import java.time.Instant;

public record MemoryMessageResponse(String role, String content, Instant createdAt) {
}
