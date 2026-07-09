package com.example.chapter13.ai.mcp;

import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.McpEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class McpEvaluationTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final AcmePlatformContextService acmePlatformContextService = new AcmePlatformContextService(baselineAssistantService);
    private final McpEvaluationService service = new McpEvaluationService(acmePlatformContextService);

    @Test
    void shouldValidateMcpCommunicationCapabilitiesAndRetryPaths() {
        var result = service.evaluateMcpSuite();

        assertThat(result.metrics().discoverySuccessRate()).isEqualTo(100);
        assertThat(result.metrics().gracefulFailureRate()).isEqualTo(100);
        assertThat(result.cases()).anyMatch(c -> "authorization".equals(c.capability()) && !c.authorized());
    }
}
