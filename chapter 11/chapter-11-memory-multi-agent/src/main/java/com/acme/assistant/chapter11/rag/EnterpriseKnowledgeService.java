package com.acme.assistant.chapter11.rag;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Encapsulates RAG ingestion and retrieval over enterprise documentation.
 */
@Service
public class EnterpriseKnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseKnowledgeService.class);

    private final VectorStore vectorStore;

    public EnterpriseKnowledgeService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest(String sourceName, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        Document document = new Document(content);
        document.getMetadata().put("source", sourceName);
        vectorStore.add(List.of(document));
    }

    public List<String> retrieve(String query, int topK) {
        List<Document> matches = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build());

        if (matches == null) {
            return List.of();
        }

        List<String> snippets = matches.stream().map(Document::getText).toList();
        log.info("RAG retrieved {} documents for query='{}'", snippets.size(), query);
        return snippets;
    }
}
