package com.acme.assistant.model;

import java.util.Map;

public record SearchResult(
        String chunkId,
        String documentId,
        String documentTitle,
        int chunkIndex,
        double score,
        String content,
        Map<String, String> metadata) {
}
