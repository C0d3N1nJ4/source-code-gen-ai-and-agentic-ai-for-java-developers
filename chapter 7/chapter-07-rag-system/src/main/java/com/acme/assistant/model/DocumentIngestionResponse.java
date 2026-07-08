package com.acme.assistant.model;

public record DocumentIngestionResponse(
        int processedDocuments,
        int indexedChunks) {
}
