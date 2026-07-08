package com.acme.assistant.chapter11.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Externalized application settings for memory and orchestration behavior.
 */
@Validated
@ConfigurationProperties(prefix = "acme.assistant")
public record AiPlatformProperties(
        @NotBlank String coordinatorSystemPrompt,
        @Min(1) int semanticTopK,
        @Min(1) int shortTermWindowSize
) {
}
