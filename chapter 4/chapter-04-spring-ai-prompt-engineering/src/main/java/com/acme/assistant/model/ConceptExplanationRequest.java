package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record ConceptExplanationRequest(
        @NotBlank(message = "topic is required")
        String topic,

        @NotBlank(message = "audience is required")
        String audience,

        @NotBlank(message = "format is required")
        String format) {
}
