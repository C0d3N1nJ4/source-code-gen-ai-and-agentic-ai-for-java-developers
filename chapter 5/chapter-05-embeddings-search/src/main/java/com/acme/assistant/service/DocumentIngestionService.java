package com.acme.assistant.service;

import com.acme.assistant.config.AcmeAssistantProperties;
import com.acme.assistant.document.DocumentChunkingService;
import com.acme.assistant.embedding.EmbeddingService;
import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.model.EmbeddedDocument;
import com.acme.assistant.model.PolicyDocument;
import com.acme.assistant.search.InMemoryVectorSearchService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DocumentIngestionService {

    private final PolicyDocumentService policyDocumentService;
    private final DocumentChunkingService documentChunkingService;
    private final EmbeddingService embeddingService;
    private final InMemoryVectorSearchService vectorSearchService;
    private final AcmeAssistantProperties properties;

    public DocumentIngestionService(
            PolicyDocumentService policyDocumentService,
            DocumentChunkingService documentChunkingService,
            EmbeddingService embeddingService,
            InMemoryVectorSearchService vectorSearchService,
            AcmeAssistantProperties properties) {
        this.policyDocumentService = policyDocumentService;
        this.documentChunkingService = documentChunkingService;
        this.embeddingService = embeddingService;
        this.vectorSearchService = vectorSearchService;
        this.properties = properties;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        int chunkSize = request.chunkSize() != null ? request.chunkSize() : properties.defaultChunkSize();
        int overlap = request.overlap() != null ? request.overlap() : properties.defaultOverlap();

        List<PolicyDocument> sourceDocuments = policyDocumentService.loadDocuments(request.documentIds());
        List<EmbeddedDocument> embeddedDocuments = new ArrayList<>();

        for (PolicyDocument sourceDocument : sourceDocuments) {
            List<DocumentChunk> chunks = documentChunkingService.chunkDocument(sourceDocument, chunkSize, overlap);
            for (DocumentChunk chunk : chunks) {
                float[] vector = embeddingService.embedDocumentChunk(chunk);
                embeddedDocuments.add(new EmbeddedDocument(chunk, vector));
            }
        }

        vectorSearchService.indexDocuments(embeddedDocuments);

        return new DocumentIngestionResponse(
                sourceDocuments.size(),
                embeddedDocuments.size(),
                vectorSearchService.totalIndexedChunks(),
                embeddingService.embeddingModelName(),
                sourceDocuments.stream().map(PolicyDocument::id).toList());
    }
}
