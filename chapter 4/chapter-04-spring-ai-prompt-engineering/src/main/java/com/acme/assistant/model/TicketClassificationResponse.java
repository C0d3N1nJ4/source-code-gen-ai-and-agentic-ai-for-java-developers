package com.acme.assistant.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TicketClassificationResponse(
        @NotBlank(message = "category cannot be blank")
        String category,

        @NotNull(message = "priority cannot be null")
        TicketPriority priority,

        @NotNull(message = "sentiment cannot be null")
        TicketSentiment sentiment,

        @NotNull(message = "requiresHumanReview cannot be null")
        Boolean requiresHumanReview,

        @NotNull(message = "confidence cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "confidence must be between 0.0 and 1.0")
        @DecimalMax(value = "1.0", inclusive = true, message = "confidence must be between 0.0 and 1.0")
        BigDecimal confidence) {
}
