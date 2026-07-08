package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank(message = "employeeId is required") String employeeId,
        @NotBlank(message = "message is required") String message
) {
}
