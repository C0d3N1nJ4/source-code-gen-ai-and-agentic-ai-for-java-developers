package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.exception.NotFoundException;
import com.acme.assistant.model.Employee;
import com.acme.assistant.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDirectoryService {

    private final EmployeeRepository employeeRepository;

    public EmployeeDirectoryService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee findEmployee(String employeeIdOrName) {
        EmployeeEntity employee = employeeRepository.findById(employeeIdOrName)
                .or(() -> employeeRepository.findByFullNameIgnoreCase(employeeIdOrName))
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeIdOrName));

        return toModel(employee);
    }

    EmployeeEntity requireEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
    }

    private Employee toModel(EmployeeEntity entity) {
        return new Employee(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getManagerId(),
                entity.getRemainingLeaveDays());
    }
}
