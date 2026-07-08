package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record PromptSafetyRequest(
        @NotBlank(message = "inputText is required")
        String inputText) {
}
