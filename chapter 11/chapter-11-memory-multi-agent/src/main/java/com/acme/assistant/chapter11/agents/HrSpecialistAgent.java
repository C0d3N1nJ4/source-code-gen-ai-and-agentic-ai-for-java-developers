package com.acme.assistant.chapter11.agents;

import com.acme.assistant.chapter11.mcp.HrMcpClient;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * HR specialist agent for leave and employee policy operations.
 */
@Component
public class HrSpecialistAgent implements SpecialistAgent {

    private final HrMcpClient hrMcpClient;

    public HrSpecialistAgent(HrMcpClient hrMcpClient) {
        this.hrMcpClient = hrMcpClient;
    }

    @Override
    public AgentType type() {
        return AgentType.HR;
    }

    @Override
    public boolean supports(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);
        return normalized.contains("leave")
                || normalized.contains("hr")
                || normalized.contains("employee");
    }

    @Override
    public AgentResult execute(AgentExecutionContext context) {
        Map<String, Object> result = hrMcpClient.submitLeave(context.userId(), "Friday", "Auto-detected from assistant workflow");
        return new AgentResult(type(), "HR actions completed for leave and policy context.", result);
    }
}
