package com.acme.assistant.search;

import com.acme.assistant.service.KnowledgeSearchService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KnowledgeSearchServiceTest {

    private final VectorStore vectorStore = mock(VectorStore.class);
    private final KnowledgeSearchService knowledgeSearchService = new KnowledgeSearchService(vectorStore);

    @Test
    void shouldMapVectorStoreResults() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(
                new Document("Carry-over leave is capped at 5 days.", Map.of("source", "leave-policy.md", "title", "Leave"))));

        var results = knowledgeSearchService.search(new KnowledgeSearchCriteria("carry leave", 3));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().source()).isEqualTo("leave-policy.md");
        assertThat(results.getFirst().title()).isEqualTo("Leave");
    }
}
