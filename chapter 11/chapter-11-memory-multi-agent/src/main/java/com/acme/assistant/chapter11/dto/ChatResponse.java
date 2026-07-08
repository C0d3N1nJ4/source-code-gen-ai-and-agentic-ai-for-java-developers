package com.acme.assistant.chapter11.dto;

import java.util.List;

/**
 * Outgoing chat response body.
 */
public record ChatResponse(
        String conversationId,
        String response,
        List<String> engagedAgents,
        long responseTimeMs,
        int estimatedPromptTokens,
        int estimatedResponseTokens
) {
}
