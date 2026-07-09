package com.example.chapter14.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
        "management.health.db.enabled=false"
})
@AutoConfigureMockMvc
class ObservabilityEndpointsIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposePublicHealthButSecureOtherActuatorEndpoints() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldExposeSecuredAiMetricsAndCorrelationHeadersForEvaluator() throws Exception {
        mockMvc.perform(get("/metrics/ai").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenMetrics.totalTokens").isNumber());

        mockMvc.perform(get("/metrics/business").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workflowSuccessRate").isNumber())
                .andExpect(jsonPath("$.businessOperations.leaveRequests").isNumber());

        mockMvc.perform(get("/api/ai-observability/metrics/report").with(httpBasic("evaluator", "evaluator-pass")))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-ID"))
                .andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void shouldPreserveCarriedForwardAssistantRuntimeEndpoints() throws Exception {
        String response = mockMvc.perform(post("/chat")
                        .with(httpBasic("EMP-1001", "employee-pass"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "userId":"EMP-1001",
                                  "message":"Estimate my travel expense for Project Orion"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedAgent").value("FINANCE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String conversationId = response.replaceAll(".*\"conversationId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/memory/{conversationId}", conversationId)
                        .with(httpBasic("EMP-1001", "employee-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.length()").value(2));
    }
}
