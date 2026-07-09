package com.example.chapter13.ai.agents;

import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.MultiAgentEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MultiAgentEvaluationTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final AcmePlatformContextService acmePlatformContextService = new AcmePlatformContextService(baselineAssistantService);
    private final MultiAgentEvaluationService service = new MultiAgentEvaluationService(acmePlatformContextService);

    @Test
    void shouldValidatePlanningRoutingDelegationAndIsolation() {
        var result = service.evaluateAgentSuite();

        assertThat(result.metrics().planningAccuracy()).isGreaterThanOrEqualTo(99);
        assertThat(result.metrics().agentIsolation()).isEqualTo(100);
        assertThat(result.cases()).anyMatch(c -> "partial-failure-handled".equals(c.outcome()));
    }
}
