package com.acme.assistant.service;

import com.acme.assistant.config.AcmeAssistantProperties;
import com.acme.assistant.embedding.EmbeddingService;
import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.EmbeddedDocument;
import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import com.acme.assistant.search.InMemoryVectorSearchService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("SemanticSearchService Tests")
class SemanticSearchServiceTest {

    @Test
    void returnsRankedResultsWithoutCallingOllama() {
        EmbeddingService embeddingService = Mockito.mock(EmbeddingService.class);
        InMemoryVectorSearchService vectorSearchService = new InMemoryVectorSearchService();
        SemanticSearchService semanticSearchService = new SemanticSearchService(
                embeddingService,
                vectorSearchService,
                new AcmeAssistantProperties(450, 60, 3));

        vectorSearchService.indexDocuments(List.of(
                new EmbeddedDocument(
                        new DocumentChunk(
                                "chunk-1",
                                "remote-work",
                                "Remote Work Policy",
                                0,
                                "Employees may work remotely up to three days per week.",
                                Map.of("department", "HR")),
                        new float[] {0.99f, 0.05f}),
                new EmbeddedDocument(
                        new DocumentChunk(
                                "chunk-2",
                                "annual-leave",
                                "Annual Leave Policy",
                                0,
                                "Employees receive 20 annual leave days.",
                                Map.of("department", "HR")),
                        new float[] {0.05f, 0.95f})));

        Mockito.when(embeddingService.embedUserQuery("Can I work from home?"))
                .thenReturn(new float[] {1.0f, 0.0f});

        SemanticSearchResponse response = semanticSearchService.search(
                new SemanticSearchRequest("Can I work from home?", 1));

        Assertions.assertEquals(1, response.totalResults());
        Assertions.assertEquals("remote-work", response.results().getFirst().documentId());
    }
}
