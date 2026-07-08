package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeaveBalanceServiceTest {

    private final EmployeeDirectoryService employeeDirectoryService = mock(EmployeeDirectoryService.class);
    private final LeaveBalanceService leaveBalanceService = new LeaveBalanceService(employeeDirectoryService);

    @Test
    void shouldReturnLeaveBalanceForEmployee() {
        when(employeeDirectoryService.requireEmployee("EMP-1001"))
                .thenReturn(new EmployeeEntity("EMP-1001", "Sarah Connor", "sarah.connor@acme.com", "EMP-2001", 14));

        var result = leaveBalanceService.getLeaveBalance("EMP-1001");

        assertThat(result.employeeId()).isEqualTo("EMP-1001");
        assertThat(result.remainingDays()).isEqualTo(14);
    }
}
