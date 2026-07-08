package com.acme.assistant.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record LeaveRequest(
        String requestId,
        @NotBlank(message = "employeeId is required") String employeeId,
        @NotNull(message = "startDate is required") @FutureOrPresent(message = "startDate must be in the present or future") LocalDate startDate,
        @NotNull(message = "endDate is required") @FutureOrPresent(message = "endDate must be in the present or future") LocalDate endDate,
        @NotBlank(message = "leaveType is required") String leaveType,
        @Positive(message = "requestedDays must be positive") Integer requestedDays,
        String status) {
}
