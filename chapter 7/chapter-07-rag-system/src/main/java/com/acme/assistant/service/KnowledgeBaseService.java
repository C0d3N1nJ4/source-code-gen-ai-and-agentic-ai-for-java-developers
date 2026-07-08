package com.acme.assistant.service;

import com.acme.assistant.document.DocumentChunker;
import com.acme.assistant.ingestion.EnterpriseDocumentCatalog;
import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.model.EnterpriseDocument;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseService {

    private static final int DEFAULT_CHUNK_SIZE = 400;

    private final VectorStore vectorStore;
    private final DocumentChunker documentChunker;
    private final EnterpriseDocumentCatalog enterpriseDocumentCatalog;

    public KnowledgeBaseService(
            VectorStore vectorStore,
            DocumentChunker documentChunker,
            EnterpriseDocumentCatalog enterpriseDocumentCatalog) {
        this.vectorStore = vectorStore;
        this.documentChunker = documentChunker;
        this.enterpriseDocumentCatalog = enterpriseDocumentCatalog;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        List<EnterpriseDocument> documents = (request.documents() == null || request.documents().isEmpty())
                ? enterpriseDocumentCatalog.defaultDocuments()
                : request.documents();

        int chunkSize = request.chunkSize() == null ? DEFAULT_CHUNK_SIZE : request.chunkSize();

        List<Document> chunks = documents.stream()
                .flatMap(document -> documentChunker.chunk(document, chunkSize).stream())
                .toList();

        vectorStore.add(chunks);

        return new DocumentIngestionResponse(documents.size(), chunks.size());
    }

}
