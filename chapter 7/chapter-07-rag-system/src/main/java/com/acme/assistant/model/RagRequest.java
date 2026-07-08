package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record RagRequest(@NotBlank(message = "question is required") String question) {
}
