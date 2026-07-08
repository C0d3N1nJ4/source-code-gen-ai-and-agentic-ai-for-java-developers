package com.acme.assistant.chapter11.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnterpriseAssistantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
        private ChatModel chatModel;

        @MockitoBean
        private VectorStore vectorStore;

        @MockitoBean
        private EmbeddingModel embeddingModel;

    @Test
    void shouldExposeAgentCatalogAndPreferenceEndpoints() throws Exception {
        org.mockito.Mockito.when(chatModel.call(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("Mocked coordination response");

        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agents").isArray());

        mockMvc.perform(post("/users/EMP-1/preferences")
                        .contentType("application/json")
                        .content("""
                                {
                                  \"preferredProgrammingLanguage\":\"Java\",
                                  \"preferredFramework\":\"Spring Boot\",
                                  \"preferredResponseStyle\":\"concise\",
                                  \"department\":\"Architecture\"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("EMP-1"));

        mockMvc.perform(get("/users/EMP-1/preferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredProgrammingLanguage").value("Java"));

        mockMvc.perform(post("/chat")
                        .contentType("application/json")
                        .content("""
                                {
                                  \"userId\":\"EMP-1\",
                                  \"message\":\"Estimate travel expenses and check policy\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").isNotEmpty())
                .andExpect(jsonPath("$.response").value("Mocked coordination response"));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").isNotEmpty());
    }
}
