package com.acme.assistant.model;

import java.util.List;

public record DocumentIngestionResponse(
        int documentsIngested,
        int chunksEmbedded,
        int totalIndexedChunks,
        String embeddingModel,
        List<String> ingestedDocumentIds) {
}
