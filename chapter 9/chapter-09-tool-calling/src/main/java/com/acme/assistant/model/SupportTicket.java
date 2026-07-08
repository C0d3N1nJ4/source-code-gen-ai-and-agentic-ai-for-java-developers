package com.acme.assistant.model;

import jakarta.validation.constraints.NotBlank;

public record SupportTicket(
        String ticketId,
        @NotBlank(message = "customerId is required") String customerId,
        @NotBlank(message = "summary is required") String summary,
        @NotBlank(message = "priority is required") String priority,
        String status) {
}
