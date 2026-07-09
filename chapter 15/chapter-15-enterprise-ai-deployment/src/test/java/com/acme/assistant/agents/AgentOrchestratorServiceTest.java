package com.acme.assistant.agents;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.observability.AiMetricsService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class AgentOrchestratorServiceTest {

    @Test
    void shouldDelegateToFinanceAgent() {
        AgentOrchestratorService service = new AgentOrchestratorService(new AiMetricsService(new SimpleMeterRegistry()));
        AgentOrchestratorService.AgentResult result = service.run("Please check budget variance in finance");
        assertThat(result.agent()).isEqualTo("Finance Agent");
    }
}
