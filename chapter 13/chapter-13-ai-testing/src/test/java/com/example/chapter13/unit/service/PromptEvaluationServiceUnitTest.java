package com.example.chapter13.unit.service;

import com.example.chapter13.dto.EvaluationRequests.PromptEvaluationRequest;
import com.example.chapter13.service.PromptEvaluationService;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PromptEvaluationServiceUnitTest {

    private final PromptEvaluationService service = new PromptEvaluationService();

    @Test
    void candidatePromptShouldOutperformBaseline() {
        var result = service.evaluatePromptVersions(new PromptEvaluationRequest(
                "You are an enterprise assistant.",
                "You are an enterprise assistant. Answer only using approved enterprise documentation. Respond concisely. Never speculate.",
                List.of(
                        "How many leave days do I receive?",
                        "Explain the travel reimbursement policy.",
                        "Summarize the API migration strategy.")));

        assertThat(result.recommendedVersion()).isEqualTo("candidate");
        assertThat(result.qualityDelta()).containsEntry("accuracy", 15);
    }
}
