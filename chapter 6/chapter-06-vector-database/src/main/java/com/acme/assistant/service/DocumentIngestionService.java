package com.acme.assistant.service;

import com.acme.assistant.config.ChunkingProperties;
import com.acme.assistant.embedding.EmbeddingModelInfoService;
import com.acme.assistant.document.DocumentChunkingService;
import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.DocumentIngestionRequest;
import com.acme.assistant.model.DocumentIngestionResponse;
import com.acme.assistant.model.PolicyDocumentPayload;
import com.acme.assistant.vector.VectorDocumentService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class DocumentIngestionService {

    private final PolicyDocumentCatalogService policyCatalogService;
    private final DocumentChunkingService chunkingService;
    private final VectorDocumentService vectorDocumentService;
    private final EmbeddingModelInfoService embeddingModelInfoService;
    private final ChunkingProperties chunkingProperties;
    private final VectorStore vectorStore;

    public DocumentIngestionService(
            PolicyDocumentCatalogService policyCatalogService,
            DocumentChunkingService chunkingService,
            VectorDocumentService vectorDocumentService,
            EmbeddingModelInfoService embeddingModelInfoService,
            ChunkingProperties chunkingProperties,
            VectorStore vectorStore) {
        this.policyCatalogService = policyCatalogService;
        this.chunkingService = chunkingService;
        this.vectorDocumentService = vectorDocumentService;
        this.embeddingModelInfoService = embeddingModelInfoService;
        this.chunkingProperties = chunkingProperties;
        this.vectorStore = vectorStore;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        List<PolicyDocumentPayload> sourceDocuments = documentsFrom(request);
        int chunkSize = request.chunkSize() == null ? chunkingProperties.defaultChunkSize() : request.chunkSize();
        int overlap = request.overlap() == null ? chunkingProperties.defaultOverlap() : request.overlap();

        List<DocumentChunk> allChunks = new ArrayList<>();
        for (PolicyDocumentPayload sourceDocument : sourceDocuments) {
            allChunks.addAll(chunkingService.chunkDocument(sourceDocument, chunkSize, overlap));
        }

        int stored = vectorDocumentService.storeChunks(allChunks);

        return new DocumentIngestionResponse(
                sourceDocuments.size(),
                stored,
                vectorStore.getClass().getSimpleName(),
                embeddingModelInfoService.modelName(),
                sourceDocuments.stream().map(PolicyDocumentPayload::documentId).toList());
    }

    private List<PolicyDocumentPayload> documentsFrom(DocumentIngestionRequest request) {
        if (request.documents() == null || request.documents().isEmpty()) {
            return policyCatalogService.defaultPolicies();
        }
        return request.documents();
    }
}
