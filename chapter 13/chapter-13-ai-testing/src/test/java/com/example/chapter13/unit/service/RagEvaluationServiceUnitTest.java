package com.example.chapter13.unit.service;

import com.example.chapter13.dto.EvaluationRequests.RagEvaluationRequest;
import com.example.chapter13.service.AcmePlatformContextService;
import com.example.chapter13.service.Chapter12BaselineAssistantService;
import com.example.chapter13.service.RagEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RagEvaluationServiceUnitTest {

    private final Chapter12BaselineAssistantService baselineAssistantService = new Chapter12BaselineAssistantService();
    private final RagEvaluationService service = new RagEvaluationService(new AcmePlatformContextService(baselineAssistantService));

    @Test
    void shouldRetrieveFinanceDomainForTravelPolicyQuestion() {
        var result = service.evaluateRetrieval(new RagEvaluationRequest(
                "What is Acme Corporation travel reimbursement policy?",
                3));

        assertThat(result.expectedDomain()).isEqualTo("Finance");
        assertThat(result.metrics().groundedness()).isGreaterThanOrEqualTo(95);
        assertThat(result.retrievedDocuments()).isNotEmpty();
    }
}
