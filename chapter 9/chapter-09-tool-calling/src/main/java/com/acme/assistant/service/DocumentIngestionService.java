package com.acme.assistant.service;

import com.acme.assistant.document.DocumentChunker;
import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.model.SourceDocument;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class DocumentIngestionService {

    private static final int DEFAULT_CHUNK_SIZE = 400;

    private final VectorStore vectorStore;
    private final DocumentChunker documentChunker;

    public DocumentIngestionService(VectorStore vectorStore, DocumentChunker documentChunker) {
        this.vectorStore = vectorStore;
        this.documentChunker = documentChunker;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        List<SourceDocument> documents = request.documents() == null ? List.of() : request.documents();
        int chunkSize = request.chunkSize() == null ? DEFAULT_CHUNK_SIZE : request.chunkSize();

        List<Document> chunks = documents.stream()
                .flatMap(document -> documentChunker.chunk(document, chunkSize).stream())
                .toList();

        if (!chunks.isEmpty()) {
            vectorStore.add(chunks);
        }

        return new DocumentIngestionResponse(documents.size(), chunks.size());
    }
}
