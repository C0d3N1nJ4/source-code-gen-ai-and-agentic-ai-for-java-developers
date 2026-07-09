package com.example.chapter13.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration,org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration",
        "management.health.db.enabled=false"
})
@AutoConfigureMockMvc
class Chapter12ContinuationEndpointsIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSupportChapter12StyleChatAndMemoryFlow() throws Exception {
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
                .andExpect(jsonPath("$.conversationId").value(conversationId))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    void shouldSupportChapter12StyleAdminAuditEndpoints() throws Exception {
        mockMvc.perform(get("/audit").with(httpBasic("admin-user", "admin-pass")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/security/events").with(httpBasic("admin-user", "admin-pass")))
                .andExpect(status().isOk());
    }
}
