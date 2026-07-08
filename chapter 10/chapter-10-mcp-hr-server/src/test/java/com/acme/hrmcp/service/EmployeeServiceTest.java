package com.acme.hrmcp.service;

import com.acme.hrmcp.entity.EmployeeEntity;
import com.acme.hrmcp.exception.EmployeeNotFoundException;
import com.acme.hrmcp.model.EmployeeRecord;
import com.acme.hrmcp.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    @Test
    void findEmployeeReturnsRecord() {
        EmployeeRepository repository = mock(EmployeeRepository.class);
        EmployeeEntity entity = new EmployeeEntity();
        entity.setEmployeeId("E1001");
        entity.setFullName("Avery Stone");
        entity.setEmail("avery.stone@acme.com");
        entity.setDepartment("Engineering");

        when(repository.findById("E1001")).thenReturn(Optional.of(entity));

        EmployeeService service = new EmployeeService(repository);
        EmployeeRecord record = service.findEmployee("E1001");

        assertEquals("E1001", record.employeeId());
        assertEquals("Engineering", record.department());
    }

    @Test
    void findEmployeeThrowsWhenMissing() {
        EmployeeRepository repository = mock(EmployeeRepository.class);
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        EmployeeService service = new EmployeeService(repository);

        assertThrows(EmployeeNotFoundException.class, () -> service.findEmployee("UNKNOWN"));
    }
}
