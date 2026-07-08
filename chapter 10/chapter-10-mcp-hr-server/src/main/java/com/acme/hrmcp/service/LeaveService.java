package com.acme.hrmcp.service;

import com.acme.hrmcp.entity.EmployeeEntity;
import com.acme.hrmcp.entity.LeaveRequestEntity;
import com.acme.hrmcp.exception.BusinessRuleException;
import com.acme.hrmcp.model.LeaveBalanceRecord;
import com.acme.hrmcp.model.LeaveRequestInput;
import com.acme.hrmcp.model.LeaveRequestRecord;
import com.acme.hrmcp.repository.EmployeeRepository;
import com.acme.hrmcp.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Validated
@Service
public class LeaveService {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveService(
            EmployeeService employeeService,
            EmployeeRepository employeeRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public LeaveBalanceRecord getLeaveBalance(@NotBlank String employeeId) {
        EmployeeEntity employee = employeeService.requireEmployee(employeeId);
        int remaining = employee.getAnnualLeaveEntitlement() - employee.getAnnualLeaveUsed();
        return new LeaveBalanceRecord(
                employee.getEmployeeId(),
                employee.getAnnualLeaveEntitlement(),
                employee.getAnnualLeaveUsed(),
                remaining);
    }

    @Transactional
    public LeaveRequestRecord submitLeaveRequest(@Valid LeaveRequestInput input) {
        if (input.endDate().isBefore(input.startDate())) {
            throw new BusinessRuleException("endDate must be on or after startDate");
        }

        EmployeeEntity employee = employeeService.requireEmployee(input.employeeId());
        int requestedDays = (int) ChronoUnit.DAYS.between(input.startDate(), input.endDate()) + 1;
        int remaining = employee.getAnnualLeaveEntitlement() - employee.getAnnualLeaveUsed();

        if (requestedDays > remaining) {
            throw new BusinessRuleException("Insufficient annual leave balance");
        }

        employee.setAnnualLeaveUsed(employee.getAnnualLeaveUsed() + requestedDays);
        employeeRepository.save(employee);

        LeaveRequestEntity request = new LeaveRequestEntity();
        request.setEmployeeId(employee.getEmployeeId());
        request.setStartDate(input.startDate());
        request.setEndDate(input.endDate());
        request.setDaysRequested(requestedDays);
        request.setStatus("SUBMITTED");
        request.setCreatedAt(OffsetDateTime.now());

        LeaveRequestEntity saved = leaveRequestRepository.save(request);

        int updatedRemaining = employee.getAnnualLeaveEntitlement() - employee.getAnnualLeaveUsed();
        return new LeaveRequestRecord(
                saved.getId().toString(),
                employee.getEmployeeId(),
                saved.getStatus(),
                "Leave request submitted for approval",
                updatedRemaining);
    }
}
