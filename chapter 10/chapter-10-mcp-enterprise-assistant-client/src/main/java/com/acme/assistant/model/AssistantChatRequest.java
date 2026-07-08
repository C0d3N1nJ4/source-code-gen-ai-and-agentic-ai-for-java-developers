package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record AssistantChatRequest(
        @NotBlank String message,
        String employeeId) {
}
