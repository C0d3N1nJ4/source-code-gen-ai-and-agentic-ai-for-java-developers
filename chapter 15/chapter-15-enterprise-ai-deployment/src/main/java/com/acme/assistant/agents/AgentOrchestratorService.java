package com.acme.assistant.agents;

import com.acme.assistant.observability.AiMetricsService;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AgentOrchestratorService {

    private final AiMetricsService metricsService;

    public AgentOrchestratorService(AiMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public AgentResult run(String instruction) {
        String normalized = instruction.toLowerCase(Locale.ROOT);
        String agent = pickAgent(normalized);
        metricsService.recordAgentExecution();
        return new AgentResult(agent, "Supervisor delegated to " + agent + " and execution completed");
    }

    private String pickAgent(String input) {
        if (input.contains("hr") || input.contains("leave")) {
            return "HR Agent";
        }
        if (input.contains("invoice") || input.contains("budget") || input.contains("finance")) {
            return "Finance Agent";
        }
        if (input.contains("incident") || input.contains("sla") || input.contains("operations")) {
            return "Operations Agent";
        }
        if (input.contains("infrastructure") || input.contains("architecture")) {
            return "Architecture Agent";
        }
        if (input.contains("laptop") || input.contains("access") || input.contains("it")) {
            return "IT Agent";
        }
        return "Supervisor Agent";
    }

    public record AgentResult(String agent, String output) {
    }
}
