package com.acme.assistant.service;

import com.acme.assistant.exception.BusinessRuleViolationException;
import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.model.LeaveRequestResult;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LeaveRequestService {

    private final Clock clock;
    private final Map<String, Integer> leaveBalances = new ConcurrentHashMap<>();

    public LeaveRequestService(Clock clock) {
        this.clock = clock;
        leaveBalances.put("EMP-1001", 12);
        leaveBalances.put("EMP-1002", 5);
        leaveBalances.put("EMP-1003", 1);
    }

    public LeaveBalance checkLeaveBalance(String employeeId) {
        return new LeaveBalance(employeeId, leaveBalances.getOrDefault(employeeId, 0));
    }

    public LeaveRequestResult submitLeaveRequest(LeaveRequest leaveRequest) {
        validateLeaveRequest(leaveRequest);

        int requestedDays = requestedDays(leaveRequest.startDate(), leaveRequest.endDate());
        int availableDays = leaveBalances.getOrDefault(leaveRequest.employeeId(), 0);
        leaveBalances.put(leaveRequest.employeeId(), availableDays - requestedDays);

        return new LeaveRequestResult(
                "LR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "SUBMITTED",
                "Leave request submitted for " + leaveRequest.startDate() + " to " + leaveRequest.endDate()
        );
    }

    private void validateLeaveRequest(LeaveRequest leaveRequest) {
        LocalDate today = LocalDate.now(clock);
        if (leaveRequest.startDate().isBefore(today)) {
            throw new BusinessRuleViolationException("Leave request cannot start in the past");
        }

        if (leaveRequest.endDate().isBefore(leaveRequest.startDate())) {
            throw new BusinessRuleViolationException("Leave request end date must be on or after start date");
        }

        int requestedDays = requestedDays(leaveRequest.startDate(), leaveRequest.endDate());
        int availableDays = leaveBalances.getOrDefault(leaveRequest.employeeId(), 0);
        if (availableDays < requestedDays) {
            throw new BusinessRuleViolationException(
                    "Insufficient leave balance. Available: " + availableDays + " day(s), requested: " + requestedDays + " day(s)"
            );
        }
    }

    private int requestedDays(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
