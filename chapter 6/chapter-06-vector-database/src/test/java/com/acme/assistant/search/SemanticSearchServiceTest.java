package com.acme.assistant.search;

import com.acme.assistant.config.SemanticSearchProperties;
import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@DisplayName("SemanticSearchService Tests")
class SemanticSearchServiceTest {

    @Test
    void performsVectorSearchWithConfigurableTopK() {
        VectorStore vectorStore = Mockito.mock(VectorStore.class);
        SearchResultMapper mapper = new SearchResultMapper();
        SemanticSearchService service = new SemanticSearchService(
                vectorStore,
                mapper,
                new SemanticSearchProperties(5));

        Mockito.when(vectorStore.similaritySearch(Mockito.any(SearchRequest.class)))
                .thenReturn(List.of(new Document(
                        "Employees may work remotely while travelling abroad when approved.",
                        Map.of(
                                "chunkId", "c1",
                                "documentId", "travel-policy",
                                "chunkIndex", 1,
                                "documentTitle", "Travel Policy",
                                "department", "Operations",
                                "category", "Travel",
                                "source", "acme://policies/operations/travel",
                                "score", 0.93))));

        SemanticSearchResponse response = service.search(new SemanticSearchRequest(
                "Can I work remotely while travelling abroad?",
                null,
                "Operations",
                "Travel"));

        Assertions.assertEquals(1, response.totalResults());
        Assertions.assertEquals("travel-policy", response.results().getFirst().documentId());

        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        Mockito.verify(vectorStore).similaritySearch(captor.capture());
        Assertions.assertEquals(5, captor.getValue().getTopK());
                Assertions.assertNotNull(captor.getValue().getFilterExpression());
    }
}
