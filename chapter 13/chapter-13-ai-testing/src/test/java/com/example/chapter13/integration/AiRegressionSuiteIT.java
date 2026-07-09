package com.example.chapter13.integration;

import com.example.chapter13.service.EvaluationOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
    "management.health.db.enabled=false"
})
class AiRegressionSuiteIT {

    @Autowired
    private EvaluationOrchestratorService orchestratorService;

    @Test
    void shouldPassQualityGatesAndPersistResults() {
        var result = orchestratorService.runFullSuite();

        assertThat(result.qualityReport().overallStatus()).isEqualTo("READY");
        assertThat(result.qualityReport().gates()).allMatch(gate -> "PASS".equals(gate.status()));
        assertThat(result.aiMetrics().quality()).containsKey("workflowCompletionRate");
    }
}
