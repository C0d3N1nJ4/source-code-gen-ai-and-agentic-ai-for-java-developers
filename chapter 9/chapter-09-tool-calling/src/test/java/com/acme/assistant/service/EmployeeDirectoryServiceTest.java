package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.exception.NotFoundException;
import com.acme.assistant.repository.EmployeeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeDirectoryServiceTest {

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final EmployeeDirectoryService employeeDirectoryService = new EmployeeDirectoryService(employeeRepository);

    @Test
    void shouldFindEmployeeById() {
        when(employeeRepository.findById("EMP-1001"))
                .thenReturn(Optional.of(new EmployeeEntity("EMP-1001", "Sarah Connor", "sarah.connor@acme.com", "EMP-2001", 14)));

        var employee = employeeDirectoryService.findEmployee("EMP-1001");

        assertThat(employee.employeeId()).isEqualTo("EMP-1001");
        assertThat(employee.fullName()).isEqualTo("Sarah Connor");
    }

    @Test
    void shouldThrowWhenEmployeeNotFoundByIdOrName() {
        when(employeeRepository.findById("Unknown Employee")).thenReturn(Optional.empty());
        when(employeeRepository.findByFullNameIgnoreCase("Unknown Employee")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeDirectoryService.findEmployee("Unknown Employee"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not found");
    }
}
