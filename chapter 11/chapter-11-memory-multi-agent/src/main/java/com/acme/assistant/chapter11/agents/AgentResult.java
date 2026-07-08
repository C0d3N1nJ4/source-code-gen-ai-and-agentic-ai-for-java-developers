package com.acme.assistant.chapter11.agents;

import java.util.Map;

/**
 * Specialist-agent execution output.
 */
public record AgentResult(
        AgentType agentType,
        String summary,
        Map<String, Object> payload
) {
}
