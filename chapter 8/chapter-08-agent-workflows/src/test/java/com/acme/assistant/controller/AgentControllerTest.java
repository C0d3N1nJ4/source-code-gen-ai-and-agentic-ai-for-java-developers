package com.acme.assistant.controller;

import com.acme.assistant.model.AssistantRequest;
import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequestResult;
import com.acme.assistant.service.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AgentController.class)
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private AgentService agentService;

    @Test
    void shouldReturnAssistantResponse() throws Exception {
        AssistantResponse assistantResponse = new AssistantResponse(
                "ACTION_COMPLETED",
                "Leave request has been submitted for 2026-07-10.",
                "Policy context",
                new LeaveBalance("EMP-1001", 12),
                new LeaveRequestResult("LR-1234ABCD", "SUBMITTED", "Submitted")
        );
        when(agentService.handle(any(AssistantRequest.class))).thenReturn(assistantResponse);

        mockMvc.perform(post("/api/assistant/agent")
                        .contentType(APPLICATION_JSON)
                .content("{\"employeeId\":\"EMP-1001\",\"message\":\"Book annual leave for next Friday\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTION_COMPLETED"))
                .andExpect(jsonPath("$.leaveRequestResult.status").value("SUBMITTED"));
    }

    @Test
    void shouldReturnBadRequestForValidationFailure() throws Exception {
        mockMvc.perform(post("/api/assistant/agent")
                        .contentType(APPLICATION_JSON)
                        .content("{\"employeeId\":\"\",\"message\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
