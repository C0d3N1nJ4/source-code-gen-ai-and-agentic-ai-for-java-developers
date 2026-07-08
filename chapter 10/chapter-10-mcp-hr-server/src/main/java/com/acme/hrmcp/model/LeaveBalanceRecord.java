package com.acme.hrmcp.model;

public record LeaveBalanceRecord(
        String employeeId,
        int annualLeaveEntitlement,
        int annualLeaveUsed,
        int remainingLeaveDays) {
}
