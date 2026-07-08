package com.acme.hrmcp.service;

import com.acme.hrmcp.entity.EmployeeEntity;
import com.acme.hrmcp.exception.EmployeeNotFoundException;
import com.acme.hrmcp.model.EmployeeRecord;
import com.acme.hrmcp.repository.EmployeeRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeRecord findEmployee(@NotBlank String employeeId) {
        EmployeeEntity employee = requireEmployee(employeeId);
        return new EmployeeRecord(
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartment());
    }

    EmployeeEntity requireEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }
}
