package com.acme.assistant.vector;

import com.acme.assistant.model.DocumentChunk;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class VectorDocumentService {

    private final VectorStore vectorStore;

    public VectorDocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public int storeChunks(List<DocumentChunk> chunks) {
        List<Document> documents = chunks.stream().map(this::toDocument).toList();
        vectorStore.add(documents);
        return documents.size();
    }

    private Document toDocument(DocumentChunk chunk) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("chunkId", chunk.chunkId());
        metadata.put("documentId", chunk.documentId());
        metadata.put("chunkIndex", chunk.chunkIndex());
        metadata.put("documentTitle", chunk.metadata().documentTitle());
        metadata.put("department", chunk.metadata().department());
        metadata.put("category", chunk.metadata().category());
        metadata.put("source", chunk.metadata().source());

        return new Document(chunk.content(), metadata);
    }
}
