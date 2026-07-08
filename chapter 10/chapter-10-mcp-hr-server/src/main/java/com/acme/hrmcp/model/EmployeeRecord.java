package com.acme.hrmcp.model;

public record EmployeeRecord(
        String employeeId,
        String fullName,
        String email,
        String department) {
}
