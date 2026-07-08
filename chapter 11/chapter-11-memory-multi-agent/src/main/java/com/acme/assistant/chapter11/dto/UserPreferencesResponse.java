package com.acme.assistant.chapter11.dto;

import java.time.OffsetDateTime;

/**
 * API representation of stored user preferences.
 */
public record UserPreferencesResponse(
        String userId,
        String preferredProgrammingLanguage,
        String preferredFramework,
        String preferredResponseStyle,
        String department,
        OffsetDateTime updatedAt
) {
}
