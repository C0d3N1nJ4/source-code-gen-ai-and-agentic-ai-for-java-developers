package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record DocumentSummaryRequest(
        @NotBlank(message = "documentText is required")
        String documentText,

        @NotBlank(message = "format is required")
        String format) {
}
