package com.acme.assistant.model;

import java.time.LocalDateTime;

public record Meeting(
        String meetingId,
        String organizerId,
        String attendee,
        String title,
        LocalDateTime startTime,
        int durationMinutes,
        String status) {
}
