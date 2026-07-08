package com.acme.assistant.model;

public record EmbeddedDocument(
        DocumentChunk chunk,
        float[] embedding) {
}
