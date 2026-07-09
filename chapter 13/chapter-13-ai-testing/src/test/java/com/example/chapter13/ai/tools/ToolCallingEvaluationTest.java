package com.example.chapter13.ai.tools;

import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.ToolCallingEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToolCallingEvaluationTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final ToolCallingEvaluationService service = new ToolCallingEvaluationService(new AcmePlatformContextService(baselineAssistantService));

    @Test
    void shouldValidateToolSelectionAuthorizationAndFailures() {
        var result = service.evaluateToolCallingSuite();

        assertThat(result.metrics().toolSelectionAccuracy()).isEqualTo(100);
        assertThat(result.metrics().authorizationEnforcement()).isEqualTo(100);
        assertThat(result.cases()).anyMatch(c -> "restartServer".equals(c.selectedTool()) && !c.authorized());
    }
}
