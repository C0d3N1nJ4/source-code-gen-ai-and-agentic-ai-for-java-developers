package com.acme.assistant.tool;

import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.service.LeaveBalanceService;
import com.acme.assistant.service.LeaveRequestService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class LeaveTools {

    private final LeaveBalanceService leaveBalanceService;
    private final LeaveRequestService leaveRequestService;

    public LeaveTools(LeaveBalanceService leaveBalanceService, LeaveRequestService leaveRequestService) {
        this.leaveBalanceService = leaveBalanceService;
        this.leaveRequestService = leaveRequestService;
    }

    @Tool(description = "Retrieve an employee's remaining annual leave balance by employee identifier.")
    public LeaveBalance getLeaveBalance(String employeeId) {
        return leaveBalanceService.getLeaveBalance(employeeId);
    }

    @Tool(description = "Submit an annual leave request including employee id, dates, and leave type.")
    public LeaveRequest submitLeaveRequest(LeaveRequest request) {
        return leaveRequestService.submitLeaveRequest(request);
    }
}
