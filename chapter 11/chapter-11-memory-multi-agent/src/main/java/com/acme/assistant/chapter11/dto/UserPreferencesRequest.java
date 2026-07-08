package com.acme.assistant.chapter11.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request contract for user preference upsert.
 */
public record UserPreferencesRequest(
        @NotBlank String preferredProgrammingLanguage,
        @NotBlank String preferredFramework,
        @NotBlank String preferredResponseStyle,
        @NotBlank String department
) {
}
