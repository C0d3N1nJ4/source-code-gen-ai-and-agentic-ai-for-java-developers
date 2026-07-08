package com.acme.assistant.model;

public record Employee(
        String employeeId,
        String fullName,
        String email,
        String managerId,
        int remainingLeaveDays) {
}
