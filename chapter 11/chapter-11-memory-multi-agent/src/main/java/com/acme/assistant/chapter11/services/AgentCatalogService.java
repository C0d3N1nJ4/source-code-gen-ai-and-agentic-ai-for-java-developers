package com.acme.assistant.chapter11.services;

import com.acme.assistant.chapter11.agents.AgentType;
import com.acme.assistant.chapter11.dto.AgentsResponse;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides static metadata describing available specialist agents.
 */
@Service
public class AgentCatalogService {

    public AgentsResponse listAgents() {
        return new AgentsResponse(List.of(
                new AgentsResponse.AgentDescriptor(AgentType.HR.name(),
                        "Leave requests, HR policies, employee lookup",
                        "RAG, Tool Calling, MCP"),
                new AgentsResponse.AgentDescriptor(AgentType.FINANCE.name(),
                        "Expense claims, invoice lookup, budget and travel estimates",
                        "Tool Calling, MCP"),
                new AgentsResponse.AgentDescriptor(AgentType.KNOWLEDGE.name(),
                        "Enterprise docs, architecture standards, coding guidelines",
                        "RAG, PGVector"),
                new AgentsResponse.AgentDescriptor(AgentType.OPERATIONS.name(),
                        "Deployments, infrastructure monitoring, restart and health operations",
                        "MCP")));
    }
}
