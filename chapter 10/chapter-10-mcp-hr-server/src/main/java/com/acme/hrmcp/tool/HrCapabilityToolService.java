package com.acme.hrmcp.tool;

import com.acme.hrmcp.model.EmployeeRecord;
import com.acme.hrmcp.model.HrPolicyRecord;
import com.acme.hrmcp.model.LeaveBalanceRecord;
import com.acme.hrmcp.model.LeaveRequestInput;
import com.acme.hrmcp.model.LeaveRequestRecord;
import com.acme.hrmcp.service.EmployeeService;
import com.acme.hrmcp.service.HrPolicyService;
import com.acme.hrmcp.service.LeaveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class HrCapabilityToolService {

    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final HrPolicyService hrPolicyService;

    public HrCapabilityToolService(EmployeeService employeeService, LeaveService leaveService, HrPolicyService hrPolicyService) {
        this.employeeService = employeeService;
        this.leaveService = leaveService;
        this.hrPolicyService = hrPolicyService;
    }

    @McpTool(name = "findEmployee", description = "Find an employee profile by employeeId.")
    public EmployeeRecord findEmployee(@McpToolParam(description = "Employee id to look up") @NotBlank String employeeId) {
        return employeeService.findEmployee(employeeId);
    }

    @McpTool(name = "getLeaveBalance", description = "Get annual leave balance for an employeeId.")
    public LeaveBalanceRecord getLeaveBalance(@McpToolParam(description = "Employee id to check annual leave for") @NotBlank String employeeId) {
        return leaveService.getLeaveBalance(employeeId);
    }

    @McpTool(name = "submitLeaveRequest", description = "Submit an annual leave request with employeeId, startDate, endDate, and reason.")
    public LeaveRequestRecord submitLeaveRequest(@Valid LeaveRequestInput input) {
        return leaveService.submitLeaveRequest(input);
    }

    @McpTool(name = "searchHrPolicies", description = "Search HR policy documents by free-text query.")
    public List<HrPolicyRecord> searchHrPolicies(@McpToolParam(description = "Policy search query") @NotBlank String query) {
        return hrPolicyService.searchHrPolicies(query);
    }
}
