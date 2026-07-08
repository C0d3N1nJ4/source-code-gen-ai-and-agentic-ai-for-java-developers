package com.acme.assistant.model;

import java.util.List;

public record DocumentIngestionResponse(
        int documentsIngested,
        int chunksStored,
        String vectorStore,
        String embeddingModel,
        List<String> documentIds) {
}
