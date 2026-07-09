package com.example.chapter14.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ObservabilityRequests {

    private ObservabilityRequests() {
    }

    public record TraceSimulationRequest(
            @NotBlank String conversationId,
            @NotBlank String userId,
            @NotBlank String goal,
            @NotNull Boolean includeFailurePath) {
    }

    public record StructuredLogRequest(
            @NotBlank String traceId,
            @NotBlank String conversationId,
            @NotBlank String promptVersion,
            @NotNull Boolean includeSensitiveMetadata) {
    }
}