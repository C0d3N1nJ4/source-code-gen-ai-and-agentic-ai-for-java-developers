package com.acme.assistant.chapter11.dto;

import java.util.List;

/**
 * Conversation memory view for retrieval endpoint.
 */
public record MemoryResponse(
        String conversationId,
        List<MemoryTurn> turns
) {
    /**
     * Single stored message turn.
     */
    public record MemoryTurn(String role, String content, String createdAt) {
    }
}
