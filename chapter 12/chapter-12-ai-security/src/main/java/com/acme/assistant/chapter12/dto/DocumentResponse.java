package com.acme.assistant.chapter12.dto;

public record DocumentResponse(
        Long id,
        String title,
        String classification,
        String ownerUserId,
        boolean trustedMetadata) {
}
