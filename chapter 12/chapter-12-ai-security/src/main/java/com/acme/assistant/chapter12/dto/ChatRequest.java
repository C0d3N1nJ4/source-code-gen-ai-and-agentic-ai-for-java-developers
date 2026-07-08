package com.acme.assistant.chapter12.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "message is required") String message) {
}
