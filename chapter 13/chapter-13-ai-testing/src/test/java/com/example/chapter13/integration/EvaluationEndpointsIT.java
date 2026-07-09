package com.example.chapter13.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
        "management.health.db.enabled=false"
})
class EvaluationEndpointsIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void securedEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/evaluations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldExposeSecuredEvaluationEndpointsForEvaluatorRole() throws Exception {
        mockMvc.perform(get("/evaluations").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.project").value("Acme Enterprise Assistant"));

        mockMvc.perform(post("/evaluations/prompts").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedVersion").value("candidate"));

        mockMvc.perform(get("/metrics/ai").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quality.promptAccuracy").isNumber());
    }
}
