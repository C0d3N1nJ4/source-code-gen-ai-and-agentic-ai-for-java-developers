package com.example.chapter13.service;

import com.example.chapter13.config.AiQualityProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationPipelineServiceTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final PromptEvaluationService promptEvaluationService = new PromptEvaluationService();
    private final AcmePlatformContextService acmePlatformContextService = new AcmePlatformContextService(baselineAssistantService);
    private final RagEvaluationService ragEvaluationService = new RagEvaluationService(acmePlatformContextService);
    private final WorkflowEvaluationService workflowEvaluationService = new WorkflowEvaluationService(acmePlatformContextService);
    private final ConversationEvaluationService conversationEvaluationService = new ConversationEvaluationService(acmePlatformContextService);
    private final ToolCallingEvaluationService toolCallingEvaluationService = new ToolCallingEvaluationService(acmePlatformContextService);
    private final AiQualityProperties qualityProperties = new AiQualityProperties(95, 98, 99, 99, 0, 2);

    @Test
    void shouldProduceReadyReleaseReport() {
        EvaluationPipelineService service = new EvaluationPipelineService(
                promptEvaluationService,
                ragEvaluationService,
                workflowEvaluationService,
            conversationEvaluationService,
            toolCallingEvaluationService,
            qualityProperties);

        var report = service.buildReleaseReport();

        assertThat(report.overallStatus()).isEqualTo("READY");
        assertThat(report.gates()).allMatch(gate -> "PASS".equals(gate.status()));
        assertThat(report.headlineMetrics()).containsEntry("hallucinationRate", 1);
        assertThat(report.headlineMetrics()).containsEntry("memoryIsolation", 100);
    }
}