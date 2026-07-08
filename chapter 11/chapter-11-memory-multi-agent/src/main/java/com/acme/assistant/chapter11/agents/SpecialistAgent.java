package com.acme.assistant.chapter11.agents;

/**
 * Contract implemented by all specialist agents.
 */
public interface SpecialistAgent {

    AgentType type();

    boolean supports(String request);

    AgentResult execute(AgentExecutionContext context);
}
