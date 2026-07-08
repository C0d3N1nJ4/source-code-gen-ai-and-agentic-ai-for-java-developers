package com.acme.hrmcp.model;

public record LeaveRequestRecord(
        String requestId,
        String employeeId,
        String status,
        String message,
        int remainingLeaveDays) {
}
