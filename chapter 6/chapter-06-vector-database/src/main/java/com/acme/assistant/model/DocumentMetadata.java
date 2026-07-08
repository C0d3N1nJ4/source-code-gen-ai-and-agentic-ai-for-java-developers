package com.acme.assistant.model;

public record DocumentMetadata(
        String documentTitle,
        String department,
        String category,
        String source) {
}
