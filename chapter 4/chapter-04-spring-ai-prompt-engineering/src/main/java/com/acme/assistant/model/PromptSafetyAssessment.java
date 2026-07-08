package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PromptSafetyAssessment(
        @NotNull(message = "safe cannot be null")
        Boolean safe,

        @NotBlank(message = "reason cannot be blank")
        String reason,

        @NotBlank(message = "detectedRisk cannot be blank")
        String detectedRisk,

        @NotBlank(message = "recommendedAction cannot be blank")
        String recommendedAction) {
}
