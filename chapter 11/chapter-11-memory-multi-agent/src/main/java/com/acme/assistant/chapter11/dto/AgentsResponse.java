package com.acme.assistant.chapter11.dto;

import java.util.List;

/**
 * API response describing available specialist agents.
 */
public record AgentsResponse(List<AgentDescriptor> agents) {

    /**
     * Agent descriptor details.
     */
    public record AgentDescriptor(String name, String responsibilities, String integrations) {
    }
}
