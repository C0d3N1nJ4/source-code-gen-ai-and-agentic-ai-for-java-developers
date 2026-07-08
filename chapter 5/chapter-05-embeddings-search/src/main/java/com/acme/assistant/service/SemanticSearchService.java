package com.acme.assistant.service;

import com.acme.assistant.config.AcmeAssistantProperties;
import com.acme.assistant.embedding.EmbeddingService;
import com.acme.assistant.model.SearchResult;
import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import com.acme.assistant.search.InMemoryVectorSearchService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final InMemoryVectorSearchService vectorSearchService;
    private final AcmeAssistantProperties properties;

    public SemanticSearchService(
            EmbeddingService embeddingService,
            InMemoryVectorSearchService vectorSearchService,
            AcmeAssistantProperties properties) {
        this.embeddingService = embeddingService;
        this.vectorSearchService = vectorSearchService;
        this.properties = properties;
    }

    public SemanticSearchResponse search(SemanticSearchRequest request) {
        int topK = request.topK() != null ? request.topK() : properties.defaultTopK();
        float[] queryVector = embeddingService.embedUserQuery(request.query());
        List<SearchResult> results = vectorSearchService.semanticSearch(queryVector, topK);

        return new SemanticSearchResponse(request.query(), results.size(), results);
    }
}
