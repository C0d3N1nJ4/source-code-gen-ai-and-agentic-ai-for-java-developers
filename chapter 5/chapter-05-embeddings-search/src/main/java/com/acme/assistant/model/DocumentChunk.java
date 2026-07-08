package com.acme.assistant.model;

import java.util.Map;

public record DocumentChunk(
        String chunkId,
        String documentId,
        String documentTitle,
        int chunkIndex,
        String content,
        Map<String, String> metadata) {
}
