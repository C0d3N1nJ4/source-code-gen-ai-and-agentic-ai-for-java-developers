package com.acme.assistant.model;

public record LeaveRequestResult(
        String requestId,
        String status,
        String message
) {
}
