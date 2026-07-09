package com.example.chapter13.ai.workflows;

import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.WorkflowEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowEvaluationTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final WorkflowEvaluationService service = new WorkflowEvaluationService(new AcmePlatformContextService(baselineAssistantService));

    @Test
    void shouldValidateEnterpriseWorkflowCompletionAndFailureHandling() {
        var result = service.evaluateEnterpriseWorkflowSuite();

        assertThat(result.metrics().workflowCompletionRate()).isEqualTo(100);
        assertThat(result.metrics().gracefulFailureRate()).isEqualTo(100);
        assertThat(result.cases()).hasSizeGreaterThanOrEqualTo(4);
    }
}
