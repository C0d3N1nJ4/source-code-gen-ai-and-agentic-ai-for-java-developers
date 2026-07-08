package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record PolicyDocumentPayload(
        String documentId,
        @NotBlank(message = "title is required") String title,
        @NotBlank(message = "content is required") String content,
        @NotBlank(message = "department is required") String department,
        @NotBlank(message = "category is required") String category,
        @NotBlank(message = "source is required") String source) {
}
