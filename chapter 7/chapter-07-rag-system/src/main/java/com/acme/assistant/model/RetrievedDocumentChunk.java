package com.acme.assistant.model;

import java.util.Map;

public record RetrievedDocumentChunk(
        String id,
        String text,
        String source,
        String title,
        Integer chunkIndex,
        Map<String, Object> metadata) {
}
