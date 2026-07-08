package com.acme.assistant.tool;

import com.acme.assistant.model.Employee;
import com.acme.assistant.service.EmployeeDirectoryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class EmployeeTools {

    private final EmployeeDirectoryService employeeDirectoryService;

    public EmployeeTools(EmployeeDirectoryService employeeDirectoryService) {
        this.employeeDirectoryService = employeeDirectoryService;
    }

    @Tool(description = "Find employee details by employee id or full name.")
    public Employee findEmployee(String employeeIdOrName) {
        return employeeDirectoryService.findEmployee(employeeIdOrName);
    }
}
