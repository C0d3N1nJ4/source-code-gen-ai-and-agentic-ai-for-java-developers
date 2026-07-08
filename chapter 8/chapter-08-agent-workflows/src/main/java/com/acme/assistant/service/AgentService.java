package com.acme.assistant.service;

import com.acme.assistant.agent.AgentDecision;
import com.acme.assistant.agent.LeaveAgent;
import com.acme.assistant.model.AssistantRequest;
import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequest;
import com.acme.assistant.model.LeaveRequestResult;
import com.acme.assistant.rag.RagKnowledgeService;
import com.acme.assistant.tools.HrTool;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AgentService {

    private final LeaveAgent leaveAgent;
    private final RagKnowledgeService ragKnowledgeService;
    private final HrTool hrTool;

    public AgentService(LeaveAgent leaveAgent, RagKnowledgeService ragKnowledgeService, HrTool hrTool) {
        this.leaveAgent = leaveAgent;
        this.ragKnowledgeService = ragKnowledgeService;
        this.hrTool = hrTool;
    }

    public AssistantResponse handle(AssistantRequest request) {
        String policyContext = ragKnowledgeService.getLeavePolicyContext();
        AgentDecision decision = leaveAgent.decide(request.message(), policyContext);

        if (!"SUBMIT_LEAVE_REQUEST".equalsIgnoreCase(decision.intent())) {
            return new AssistantResponse(
                    "ANSWER_ONLY",
                    "I can explain leave policy, but I did not detect a concrete leave-booking action to execute.",
                    policyContext,
                    hrTool.checkLeaveBalance(request.employeeId()),
                    null
            );
        }

        if ("UNKNOWN".equalsIgnoreCase(decision.targetDate())) {
            return new AssistantResponse(
                    "NEEDS_CLARIFICATION",
                    "I understand you want to book leave, but I need a specific date before submitting the request.",
                    policyContext,
                    hrTool.checkLeaveBalance(request.employeeId()),
                    null
            );
        }

        LocalDate leaveDate = LocalDate.parse(decision.targetDate());
        LeaveBalance leaveBalance = hrTool.checkLeaveBalance(request.employeeId());
        LeaveRequest leaveRequest = new LeaveRequest(request.employeeId(), leaveDate, leaveDate, "Annual leave request from assistant workflow");
        LeaveRequestResult result = hrTool.submitLeaveRequest(leaveRequest);

        return new AssistantResponse(
                "ACTION_COMPLETED",
                "Leave request has been submitted for " + leaveDate + ".",
                policyContext,
                leaveBalance,
                result
        );
    }
}
