package com.acme.assistant.chapter11.agents;

import com.acme.assistant.chapter11.mcp.FinanceMcpClient;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Finance specialist agent for expense and budget operations.
 */
@Component
public class FinanceSpecialistAgent implements SpecialistAgent {

    private final FinanceMcpClient financeMcpClient;

    public FinanceSpecialistAgent(FinanceMcpClient financeMcpClient) {
        this.financeMcpClient = financeMcpClient;
    }

    @Override
    public AgentType type() {
        return AgentType.FINANCE;
    }

    @Override
    public boolean supports(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);
        return normalized.contains("expense")
                || normalized.contains("travel")
                || normalized.contains("invoice")
                || normalized.contains("budget");
    }

    @Override
    public AgentResult execute(AgentExecutionContext context) {
        Map<String, Object> result = financeMcpClient.estimateTravelExpenses("Amsterdam", 3);
        return new AgentResult(type(), "Finance estimate generated for travel and expenses.", result);
    }
}
