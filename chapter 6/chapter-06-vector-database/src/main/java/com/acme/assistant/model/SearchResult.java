package com.acme.assistant.model;

public record SearchResult(
        String chunkId,
        String documentId,
        int chunkIndex,
        String content,
        DocumentMetadata metadata,
        Double similarityScore) {
}
