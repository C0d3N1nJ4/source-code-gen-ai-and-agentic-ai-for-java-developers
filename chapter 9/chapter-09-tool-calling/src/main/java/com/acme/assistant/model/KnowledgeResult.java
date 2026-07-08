package com.acme.assistant.model;

import java.util.Map;

public record KnowledgeResult(
        String chunkId,
        String content,
        String source,
        String title,
        Map<String, Object> metadata) {
}
