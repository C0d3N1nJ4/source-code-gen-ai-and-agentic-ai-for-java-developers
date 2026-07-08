package com.acme.assistant.service;

import com.acme.assistant.agent.AgentDecision;
import com.acme.assistant.agent.LeaveAgent;
import com.acme.assistant.model.AssistantRequest;
import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequestResult;
import com.acme.assistant.rag.RagKnowledgeService;
import com.acme.assistant.tools.HrTool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentServiceTest {

    private final LeaveAgent leaveAgent = mock(LeaveAgent.class);
    private final RagKnowledgeService ragKnowledgeService = mock(RagKnowledgeService.class);
    private final HrTool hrTool = mock(HrTool.class);

    private final AgentService agentService = new AgentService(leaveAgent, ragKnowledgeService, hrTool);

    @Test
    void shouldReturnAnswerOnlyWhenIntentIsNotActionable() {
        AssistantRequest request = new AssistantRequest("EMP-1001", "What is the leave policy?");
        when(ragKnowledgeService.getLeavePolicyContext()).thenReturn("Policy context");
        when(leaveAgent.decide(request.message(), "Policy context"))
                .thenReturn(new AgentDecision("ANSWER_ONLY", "UNKNOWN", "No action intent"));
        when(hrTool.checkLeaveBalance("EMP-1001")).thenReturn(new LeaveBalance("EMP-1001", 12));

        AssistantResponse response = agentService.handle(request);

        assertEquals("ANSWER_ONLY", response.status());
        assertNotNull(response.leaveBalance());
        assertNull(response.leaveRequestResult());
        verify(hrTool, never()).submitLeaveRequest(any());
    }

    @Test
    void shouldRequestClarificationWhenDateIsUnknown() {
        AssistantRequest request = new AssistantRequest("EMP-1001", "Book annual leave soon");
        when(ragKnowledgeService.getLeavePolicyContext()).thenReturn("Policy context");
        when(leaveAgent.decide(request.message(), "Policy context"))
                .thenReturn(new AgentDecision("SUBMIT_LEAVE_REQUEST", "UNKNOWN", "Missing date"));
        when(hrTool.checkLeaveBalance("EMP-1001")).thenReturn(new LeaveBalance("EMP-1001", 12));

        AssistantResponse response = agentService.handle(request);

        assertEquals("NEEDS_CLARIFICATION", response.status());
        assertNull(response.leaveRequestResult());
        verify(hrTool, never()).submitLeaveRequest(any());
    }

    @Test
    void shouldExecuteLeaveRequestWhenIntentAndDateAreValid() {
        AssistantRequest request = new AssistantRequest("EMP-1002", "Book annual leave for next Friday");
        when(ragKnowledgeService.getLeavePolicyContext()).thenReturn("Policy context");
        when(leaveAgent.decide(request.message(), "Policy context"))
                .thenReturn(new AgentDecision("SUBMIT_LEAVE_REQUEST", "2026-07-10", "Detected leave request"));
        when(hrTool.checkLeaveBalance("EMP-1002")).thenReturn(new LeaveBalance("EMP-1002", 5));
        when(hrTool.submitLeaveRequest(any()))
                .thenReturn(new LeaveRequestResult("LR-ABCD1234", "SUBMITTED", "Submitted"));

        AssistantResponse response = agentService.handle(request);

        assertEquals("ACTION_COMPLETED", response.status());
        assertNotNull(response.leaveRequestResult());
        assertEquals("SUBMITTED", response.leaveRequestResult().status());
        verify(hrTool).submitLeaveRequest(any());
        verify(hrTool).checkLeaveBalance(eq("EMP-1002"));
    }
}
