package com.acme.assistant.chapter11.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Incoming chat request body.
 */
public record ChatRequest(
        @NotBlank String userId,
        @NotBlank String message
) {
}
