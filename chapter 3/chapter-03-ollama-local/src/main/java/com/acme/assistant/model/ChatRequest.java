package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "message must not be blank")
        @Size(max = 1000, message = "message must be at most 1000 characters")
        String message) {
}
