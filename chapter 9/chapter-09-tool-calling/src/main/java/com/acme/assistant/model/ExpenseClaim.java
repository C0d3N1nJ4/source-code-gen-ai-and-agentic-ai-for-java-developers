package com.acme.assistant.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ExpenseClaim(
        String claimId,
        @NotBlank(message = "employeeId is required") String employeeId,
        @NotBlank(message = "category is required") String category,
        @NotNull(message = "amount is required") @DecimalMin(value = "0.01", message = "amount must be greater than zero") BigDecimal amount,
        @NotBlank(message = "currency is required") String currency,
        @NotBlank(message = "description is required") String description,
        String status) {
}
