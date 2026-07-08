package com.acme.hrmcp.service;

import com.acme.hrmcp.entity.EmployeeEntity;
import com.acme.hrmcp.entity.LeaveRequestEntity;
import com.acme.hrmcp.exception.BusinessRuleException;
import com.acme.hrmcp.model.LeaveRequestInput;
import com.acme.hrmcp.model.LeaveRequestRecord;
import com.acme.hrmcp.repository.EmployeeRepository;
import com.acme.hrmcp.repository.LeaveRequestRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeaveServiceTest {

    @Test
    void submitLeaveRequestUpdatesBalance() {
        EmployeeService employeeService = mock(EmployeeService.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        LeaveRequestRepository leaveRequestRepository = mock(LeaveRequestRepository.class);

        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId("E1001");
        employee.setAnnualLeaveEntitlement(25);
        employee.setAnnualLeaveUsed(10);

        when(employeeService.requireEmployee("E1001")).thenReturn(employee);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(leaveRequestRepository.save(any(LeaveRequestEntity.class))).thenAnswer(i -> {
            LeaveRequestEntity entity = i.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        LeaveService service = new LeaveService(employeeService, employeeRepository, leaveRequestRepository);
        LeaveRequestRecord result = service.submitLeaveRequest(
                new LeaveRequestInput("E1001", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Family event"));

        assertEquals("SUBMITTED", result.status());
        assertEquals(13, result.remainingLeaveDays());
        verify(employeeRepository).save(any(EmployeeEntity.class));
    }

    @Test
    void submitLeaveRequestRejectsInsufficientBalance() {
        EmployeeService employeeService = mock(EmployeeService.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        LeaveRequestRepository leaveRequestRepository = mock(LeaveRequestRepository.class);

        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId("E1001");
        employee.setAnnualLeaveEntitlement(12);
        employee.setAnnualLeaveUsed(10);

        when(employeeService.requireEmployee("E1001")).thenReturn(employee);

        LeaveService service = new LeaveService(employeeService, employeeRepository, leaveRequestRepository);

        assertThrows(BusinessRuleException.class, () ->
                service.submitLeaveRequest(new LeaveRequestInput(
                        "E1001",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        "Vacation")));
    }
}
