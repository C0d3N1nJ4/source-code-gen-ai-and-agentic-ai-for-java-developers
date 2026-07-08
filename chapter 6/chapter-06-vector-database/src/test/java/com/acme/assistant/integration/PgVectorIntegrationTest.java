package com.acme.assistant.integration;

import com.acme.assistant.AcmeAssistantChapter6Application;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = AcmeAssistantChapter6Application.class)
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("PGVector Integration Test")
class PgVectorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg17")
            .withDatabaseName("acme_ai")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.ai.vectorstore.pgvector.initialize-schema", () -> "true");
        registry.add("spring.ai.vectorstore.pgvector.dimensions", () -> "768");
        registry.add("spring.ai.ollama.base-url", () -> "http://localhost:11434");
        registry.add("spring.ai.ollama.embedding.options.model", () -> "nomic-embed-text");
    }

    @Autowired
    private VectorStore vectorStore;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @BeforeEach
    void setupEmbeddingModel() {
        Mockito.when(embeddingModel.embed(Mockito.anyString()))
                .thenAnswer(invocation -> embeddingFor(invocation.getArgument(0, String.class)));

        Mockito.when(embeddingModel.embed(Mockito.anyList()))
                .thenAnswer(invocation -> {
                    List<String> input = invocation.getArgument(0);
                    return input.stream().map(this::embeddingFor).toList();
                });
    }

    @Test
    void pgvectorStoresAndRetrievesSemanticallySimilarChunks() {
        vectorStore.add(List.of(
                new Document("Employees may work remotely while travelling abroad when approved.",
                        java.util.Map.of("documentId", "travel-policy", "department", "Operations")),
                new Document("Annual leave requests must be approved by managers.",
                        java.util.Map.of("documentId", "annual-leave-policy", "department", "HR"))));

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Can I work remotely while travelling abroad?")
                        .topK(1)
                        .build());

        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals("travel-policy", results.getFirst().getMetadata().get("documentId"));
    }

    private float[] embeddingFor(String text) {
        float[] vector = new float[768];
        String normalized = text.toLowerCase();

        if (normalized.contains("remote") || normalized.contains("travel")) {
            vector[0] = 0.95f;
            vector[1] = 0.05f;
        } else if (normalized.contains("leave")) {
            vector[0] = 0.05f;
            vector[1] = 0.95f;
        } else {
            vector[0] = 0.50f;
            vector[1] = 0.50f;
        }

        return vector;
    }
}
