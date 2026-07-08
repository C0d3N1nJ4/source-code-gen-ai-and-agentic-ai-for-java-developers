package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DocumentSummaryResponse(
        @NotBlank(message = "title cannot be blank")
        String title,

        @NotBlank(message = "summary cannot be blank")
        String summary,

        @NotNull(message = "keyPoints is required")
        @NotEmpty(message = "keyPoints cannot be empty")
        List<@NotBlank(message = "keyPoints entries cannot be blank") String> keyPoints,

        @NotNull(message = "risks is required")
        List<@NotBlank(message = "risks entries cannot be blank") String> risks,

        @NotNull(message = "recommendedActions is required")
        @NotEmpty(message = "recommendedActions cannot be empty")
        List<@NotBlank(message = "recommendedActions entries cannot be blank") String> recommendedActions) {
}
