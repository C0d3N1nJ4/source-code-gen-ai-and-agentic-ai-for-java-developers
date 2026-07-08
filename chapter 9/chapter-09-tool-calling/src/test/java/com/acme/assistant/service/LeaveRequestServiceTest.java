package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.repository.EmployeeRepository;
import com.acme.assistant.repository.LeaveRequestRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeaveRequestServiceTest {

    private final EmployeeDirectoryService employeeDirectoryService = mock(EmployeeDirectoryService.class);
    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final LeaveRequestRepository leaveRequestRepository = mock(LeaveRequestRepository.class);

    private final LeaveRequestService leaveRequestService = new LeaveRequestService(
            employeeDirectoryService,
            employeeRepository,
            leaveRequestRepository);

    @Test
    void shouldSubmitLeaveRequestAndReduceBalance() {
        EmployeeEntity employee = new EmployeeEntity("EMP-1001", "Sarah Connor", "sarah.connor@acme.com", "EMP-2001", 14);
        when(employeeDirectoryService.requireEmployee("EMP-1001")).thenReturn(employee);

        LeaveRequest request = new LeaveRequest(
                null,
                "EMP-1001",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(6),
                "ANNUAL",
                null,
                null);

        LeaveRequest result = leaveRequestService.submitLeaveRequest(request);

        assertThat(result.requestId()).startsWith("LR-");
        assertThat(result.status()).isEqualTo("SUBMITTED");
        assertThat(result.requestedDays()).isEqualTo(2);
        assertThat(employee.getRemainingLeaveDays()).isEqualTo(12);
        verify(employeeRepository).save(employee);
        verify(leaveRequestRepository).save(any());
    }

    @Test
    void shouldRejectLeaveRequestWhenEndDateBeforeStartDate() {
        LeaveRequest request = new LeaveRequest(
                null,
                "EMP-1001",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(6),
                "ANNUAL",
                1,
                null);

        assertThatThrownBy(() -> leaveRequestService.submitLeaveRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endDate must not be before startDate");
    }
}
