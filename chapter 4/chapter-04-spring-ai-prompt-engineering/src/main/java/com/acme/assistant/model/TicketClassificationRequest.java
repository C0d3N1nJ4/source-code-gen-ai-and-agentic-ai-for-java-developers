package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record TicketClassificationRequest(
        @NotBlank(message = "ticketText is required")
        String ticketText,

        @NotBlank(message = "format is required")
        String format) {
}
