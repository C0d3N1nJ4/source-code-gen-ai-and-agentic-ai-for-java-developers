package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.entity.LeaveRequestEntity;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.repository.EmployeeRepository;
import com.acme.assistant.repository.LeaveRequestRepository;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaveRequestService {

    private final EmployeeDirectoryService employeeDirectoryService;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestService(
            EmployeeDirectoryService employeeDirectoryService,
            EmployeeRepository employeeRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeDirectoryService = employeeDirectoryService;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional
    public LeaveRequest submitLeaveRequest(LeaveRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }

        EmployeeEntity employee = employeeDirectoryService.requireEmployee(request.employeeId());

        int requestedDays = request.requestedDays() == null
                ? (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1
                : request.requestedDays();

        if (requestedDays <= 0) {
            throw new IllegalArgumentException("requestedDays must be positive");
        }

        if (employee.getRemainingLeaveDays() < requestedDays) {
            throw new IllegalArgumentException("Insufficient leave balance for employee " + employee.getId());
        }

        employee.setRemainingLeaveDays(employee.getRemainingLeaveDays() - requestedDays);
        employeeRepository.save(employee);

        String requestId = "LR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        LeaveRequestEntity entity = new LeaveRequestEntity(
            requestId,
            request.employeeId(),
            request.startDate(),
            request.endDate(),
            request.leaveType(),
            requestedDays,
            "SUBMITTED");
        leaveRequestRepository.save(entity);

        return new LeaveRequest(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getLeaveType(),
                entity.getRequestedDays(),
                entity.getStatus());
    }
}
