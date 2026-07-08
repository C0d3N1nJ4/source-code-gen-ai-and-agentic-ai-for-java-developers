package com.acme.assistant.chapter11.agents;

import com.acme.assistant.chapter11.mcp.OperationsMcpClient;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Operations specialist agent for infrastructure and service workflows.
 */
@Component
public class OperationsSpecialistAgent implements SpecialistAgent {

    private final OperationsMcpClient operationsMcpClient;

    public OperationsSpecialistAgent(OperationsMcpClient operationsMcpClient) {
        this.operationsMcpClient = operationsMcpClient;
    }

    @Override
    public AgentType type() {
        return AgentType.OPERATIONS;
    }

    @Override
    public boolean supports(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);
        return normalized.contains("deploy")
                || normalized.contains("infrastructure")
                || normalized.contains("monitor")
                || normalized.contains("restart")
                || normalized.contains("server health");
    }

    @Override
    public AgentResult execute(AgentExecutionContext context) {
        Map<String, Object> result = operationsMcpClient.health("enterprise-assistant");
        return new AgentResult(type(), "Operations status gathered via MCP.", result);
    }
}
