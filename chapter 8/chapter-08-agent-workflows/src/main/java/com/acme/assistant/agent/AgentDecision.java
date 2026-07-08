package com.acme.assistant.agent;

public record AgentDecision(
        String intent,
        String targetDate,
        String reason
) {
}
