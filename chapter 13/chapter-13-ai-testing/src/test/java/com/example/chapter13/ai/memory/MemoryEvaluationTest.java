package com.example.chapter13.ai.memory;

import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.ConversationEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryEvaluationTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final ConversationEvaluationService service = new ConversationEvaluationService(new AcmePlatformContextService(baselineAssistantService));

    @Test
    void shouldRetainContextAndEnforceUserIsolation() {
        var result = service.defaultEvaluation();

        assertThat(result.metrics().contextRetention()).isGreaterThanOrEqualTo(95);
        assertThat(result.metrics().userIsolation()).isEqualTo(100);
        assertThat(result.transcript()).anyMatch(line -> line.contains("Project Orion"));
    }
}
