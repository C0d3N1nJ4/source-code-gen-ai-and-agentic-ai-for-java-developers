package com.acme.assistant.service;

import com.acme.assistant.model.KnowledgeResult;
import com.acme.assistant.search.KnowledgeSearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeSearchService {

    private final VectorStore vectorStore;

    public KnowledgeSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<KnowledgeResult> search(KnowledgeSearchCriteria criteria) {
        int topK = criteria.topK() == null ? 4 : criteria.topK();

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(criteria.query())
                .topK(topK)
                .build());

        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        return documents.stream()
                .map(this::toKnowledgeResult)
                .toList();
    }

    private KnowledgeResult toKnowledgeResult(Document document) {
        Map<String, Object> metadata = document.getMetadata() == null
                ? Map.of()
                : Map.copyOf(document.getMetadata());

        return new KnowledgeResult(
                asString(metadata.get("chunkId")),
                document.getText(),
                asString(metadata.get("source")),
                asString(metadata.get("title")),
                metadata);
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
