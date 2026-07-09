package com.example.chapter13.ai.prompts;

import com.example.chapter13.service.PromptEvaluationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PromptEvaluationDatasetTest {

    private final PromptEvaluationService service = new PromptEvaluationService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldEvaluatePromptDatasetScenarios() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("datasets/prompts.json");
        List<Map<String, String>> scenarios = objectMapper.readValue(input, new TypeReference<>() {
        });

        List<String> questions = scenarios.stream().map(entry -> entry.get("question")).toList();
        var response = service.evaluatePromptVersions(new com.example.chapter13.dto.EvaluationRequests.PromptEvaluationRequest(
                "You are an enterprise assistant.",
                "You are an enterprise assistant. Answer only using approved enterprise documentation. Respond concisely. Never speculate.",
                questions));

        assertThat(response.recommendedVersion()).isEqualTo("candidate");
        assertThat(response.scenarios()).hasSize(questions.size());
    }
}
