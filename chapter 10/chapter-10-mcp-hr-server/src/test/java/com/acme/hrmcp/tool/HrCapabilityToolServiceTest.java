package com.acme.hrmcp.tool;

import com.acme.hrmcp.model.EmployeeRecord;
import com.acme.hrmcp.model.HrPolicyRecord;
import com.acme.hrmcp.model.LeaveBalanceRecord;
import com.acme.hrmcp.model.LeaveRequestInput;
import com.acme.hrmcp.model.LeaveRequestRecord;
import com.acme.hrmcp.service.EmployeeService;
import com.acme.hrmcp.service.HrPolicyService;
import com.acme.hrmcp.service.LeaveService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HrCapabilityToolServiceTest {

    @Test
    void delegatesToDomainServices() {
        EmployeeService employeeService = mock(EmployeeService.class);
        LeaveService leaveService = mock(LeaveService.class);
        HrPolicyService hrPolicyService = mock(HrPolicyService.class);

        when(employeeService.findEmployee("E1001"))
                .thenReturn(new EmployeeRecord("E1001", "Avery Stone", "avery.stone@acme.com", "Engineering"));
        when(leaveService.getLeaveBalance("E1001"))
                .thenReturn(new LeaveBalanceRecord("E1001", 25, 7, 18));
        when(leaveService.submitLeaveRequest(new LeaveRequestInput("E1001", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 10), "Personal")))
                .thenReturn(new LeaveRequestRecord("r-1", "E1001", "SUBMITTED", "ok", 17));
        when(hrPolicyService.searchHrPolicies("leave"))
                .thenReturn(List.of(new HrPolicyRecord(1L, "Annual Leave Policy", "Employees are entitled...")));

        HrCapabilityToolService tools = new HrCapabilityToolService(employeeService, leaveService, hrPolicyService);

        assertEquals("Engineering", tools.findEmployee("E1001").department());
        assertEquals(18, tools.getLeaveBalance("E1001").remainingLeaveDays());
        assertEquals("SUBMITTED", tools.submitLeaveRequest(
                new LeaveRequestInput("E1001", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 10), "Personal")).status());
        assertEquals(1, tools.searchHrPolicies("leave").size());
    }
}
