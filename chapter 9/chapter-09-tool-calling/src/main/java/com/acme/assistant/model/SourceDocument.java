package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record SourceDocument(
        @NotBlank(message = "title is required") String title,
        @NotBlank(message = "source is required") String source,
        @NotBlank(message = "content is required") String content,
        Map<String, Object> metadata) {
}
