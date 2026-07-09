package com.example.chapter13.ai.rag;

import com.example.chapter13.dto.EvaluationRequests.RagEvaluationRequest;
import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.RagEvaluationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RagEvaluationDatasetTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final RagEvaluationService service = new RagEvaluationService(new AcmePlatformContextService(baselineAssistantService));
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldEvaluateRagDatasetCasesWithExpectedDomains() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("datasets/rag.json");
        List<Map<String, String>> cases = objectMapper.readValue(input, new TypeReference<>() {
        });

        for (Map<String, String> currentCase : cases) {
            var response = service.evaluateRetrieval(new RagEvaluationRequest(currentCase.get("question"), 3));
            assertThat(response.expectedDomain()).isEqualTo(currentCase.get("expectedDomain"));
            assertThat(response.metrics().precision()).isGreaterThanOrEqualTo(66);
        }
    }
}
