package com.acme.assistant.service;

import com.acme.assistant.model.LeaveBalance;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceService {

    private final EmployeeDirectoryService employeeDirectoryService;

    public LeaveBalanceService(EmployeeDirectoryService employeeDirectoryService) {
        this.employeeDirectoryService = employeeDirectoryService;
    }

    public LeaveBalance getLeaveBalance(String employeeId) {
        var employee = employeeDirectoryService.requireEmployee(employeeId);
        return new LeaveBalance(employee.getId(), employee.getRemainingLeaveDays());
    }
}
