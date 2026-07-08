package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank(message = "message is required") String message,
        String employeeId) {
}
