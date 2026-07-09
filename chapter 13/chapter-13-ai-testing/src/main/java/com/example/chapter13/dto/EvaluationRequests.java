package com.example.chapter13.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public final class EvaluationRequests {

    private EvaluationRequests() {
    }

    public record PromptEvaluationRequest(
            @NotBlank String baselinePrompt,
            @NotBlank String candidatePrompt,
            @NotNull List<@NotBlank String> scenarios) {
    }

    public record RagEvaluationRequest(
            @NotBlank String question,
            @NotNull @Min(1) @Max(5) Integer maxDocuments) {
    }

    public record ConversationEvaluationRequest(
            @NotBlank String conversationId,
            @NotBlank String userId,
            @NotBlank String goal,
            @NotNull Boolean includeIsolationCheck) {
    }
}