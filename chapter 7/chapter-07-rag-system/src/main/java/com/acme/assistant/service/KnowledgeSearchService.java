package com.acme.assistant.service;

import com.acme.assistant.config.RagProperties;
import com.acme.assistant.model.RetrievedDocumentChunk;
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
    private final RagProperties ragProperties;

    public KnowledgeSearchService(VectorStore vectorStore, RagProperties ragProperties) {
        this.vectorStore = vectorStore;
        this.ragProperties = ragProperties;
    }

    public List<RetrievedDocumentChunk> search(KnowledgeSearchCriteria criteria) {
        int topK = criteria.topK() == null ? ragProperties.getTopK() : criteria.topK();

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(criteria.question())
                .topK(topK)
                .build());

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(this::toRetrievedChunk)
                .toList();
    }

    private RetrievedDocumentChunk toRetrievedChunk(Document document) {
        Map<String, Object> metadata = document.getMetadata() == null
                ? Map.of()
                : Map.copyOf(document.getMetadata());

        return new RetrievedDocumentChunk(
                asString(metadata.get("chunkId")),
                document.getText(),
                asString(metadata.get("source")),
                asString(metadata.get("title")),
                asInteger(metadata.get("chunkIndex")),
                metadata);
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Integer asInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value.toString());
    }

}
