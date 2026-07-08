package com.acme.assistant.model;

public record DocumentChunk(
        String chunkId,
        String documentId,
        int chunkIndex,
        String content,
        DocumentMetadata metadata) {
}
