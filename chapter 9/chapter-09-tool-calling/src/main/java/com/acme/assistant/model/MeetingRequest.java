package com.acme.assistant.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record MeetingRequest(
        @NotBlank(message = "organizerId is required") String organizerId,
        @NotBlank(message = "attendee is required") String attendee,
        @NotNull(message = "startTime is required") @Future(message = "startTime must be in the future") LocalDateTime startTime,
        @Positive(message = "durationMinutes must be positive") Integer durationMinutes,
        @NotBlank(message = "title is required") String title) {
}
