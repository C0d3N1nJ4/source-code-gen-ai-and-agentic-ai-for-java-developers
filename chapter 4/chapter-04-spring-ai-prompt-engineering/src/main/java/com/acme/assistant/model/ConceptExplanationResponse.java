package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record ConceptExplanationResponse(
        @NotBlank(message = "topic cannot be blank")
        String topic,

        @NotBlank(message = "audience cannot be blank")
        String audience,

        @NotBlank(message = "format cannot be blank")
        String format,

        @NotBlank(message = "explanation cannot be blank")
        String explanation) {
}
