package com.acme.assistant.service;

import com.acme.assistant.exception.BusinessRuleViolationException;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.model.LeaveRequestResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaveRequestServiceTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-07-03T00:00:00Z"), ZoneOffset.UTC);
    private final LeaveRequestService leaveRequestService = new LeaveRequestService(fixedClock);

    @Test
    void shouldSubmitLeaveRequestWhenBalanceIsSufficient() {
        LeaveRequest leaveRequest = new LeaveRequest(
                "EMP-1001",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 10),
                "Annual leave"
        );

        LeaveRequestResult result = leaveRequestService.submitLeaveRequest(leaveRequest);

        assertEquals("SUBMITTED", result.status());
        assertTrue(result.requestId().startsWith("LR-"));
        assertEquals(11, leaveRequestService.checkLeaveBalance("EMP-1001").availableDays());
    }

    @Test
    void shouldRejectLeaveRequestWhenBalanceIsInsufficient() {
        LeaveRequest leaveRequest = new LeaveRequest(
                "EMP-1003",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 12),
                "Family event"
        );

        BusinessRuleViolationException ex = assertThrows(
                BusinessRuleViolationException.class,
                () -> leaveRequestService.submitLeaveRequest(leaveRequest)
        );

        assertTrue(ex.getMessage().contains("Insufficient leave balance"));
    }

    @Test
    void shouldRejectLeaveRequestInPast() {
        LeaveRequest leaveRequest = new LeaveRequest(
                "EMP-1002",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 1),
                "Past date"
        );

        BusinessRuleViolationException ex = assertThrows(
                BusinessRuleViolationException.class,
                () -> leaveRequestService.submitLeaveRequest(leaveRequest)
        );

        assertEquals("Leave request cannot start in the past", ex.getMessage());
    }
}
