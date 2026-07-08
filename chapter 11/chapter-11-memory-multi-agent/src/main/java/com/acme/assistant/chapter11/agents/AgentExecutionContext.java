package com.acme.assistant.chapter11.agents;

import java.util.List;

/**
 * Immutable context passed to each specialist agent.
 */
public record AgentExecutionContext(
        String userId,
        String conversationId,
        String request,
        List<String> shortTermContext,
        List<String> longTermMemory,
        List<String> ragSnippets
) {
}
