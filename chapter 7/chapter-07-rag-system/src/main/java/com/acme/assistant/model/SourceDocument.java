package com.acme.assistant.model;

public record SourceDocument(
        String source,
        String title,
        Integer chunkIndex,
        String snippet) {
}
