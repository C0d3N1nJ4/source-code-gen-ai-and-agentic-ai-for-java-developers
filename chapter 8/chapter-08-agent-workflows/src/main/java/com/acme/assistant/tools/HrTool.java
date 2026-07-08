package com.acme.assistant.tools;

import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.model.LeaveRequestResult;
import com.acme.assistant.service.LeaveRequestService;
import org.springframework.stereotype.Component;

@Component
public class HrTool {

    private final LeaveRequestService leaveRequestService;

    public HrTool(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    public LeaveBalance checkLeaveBalance(String employeeId) {
        return leaveRequestService.checkLeaveBalance(employeeId);
    }

    public LeaveRequestResult submitLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestService.submitLeaveRequest(leaveRequest);
    }
}
