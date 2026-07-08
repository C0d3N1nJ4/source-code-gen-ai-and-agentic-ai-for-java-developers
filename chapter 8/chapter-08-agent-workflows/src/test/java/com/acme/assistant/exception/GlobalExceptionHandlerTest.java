package com.acme.assistant.exception;

import com.acme.assistant.controller.AgentController;
import com.acme.assistant.model.AssistantRequest;
import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.model.LeaveBalance;
import com.acme.assistant.model.LeaveRequestResult;
import com.acme.assistant.service.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AgentController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
        private MockMvc mockMvc;

    @Autowired
        @MockitoBean
    private AgentService agentService;

    @Test
    void shouldReturnUnprocessableEntityForBusinessRuleViolation() throws Exception {
        when(agentService.handle(any(AssistantRequest.class)))
                .thenThrow(new BusinessRuleViolationException("Insufficient leave balance"));

        mockMvc.perform(post("/api/assistant/agent")
                        .contentType(APPLICATION_JSON)
                        .content("{\"employeeId\":\"EMP-1003\",\"message\":\"Book annual leave for next Friday\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Insufficient leave balance"));
    }

    @Test
    void shouldPassThroughSuccessWhenNoException() throws Exception {
        when(agentService.handle(any(AssistantRequest.class)))
                .thenReturn(new AssistantResponse(
                        "ANSWER_ONLY",
                        "No action",
                        "Policy",
                        new LeaveBalance("EMP-1001", 12),
                        new LeaveRequestResult("LR-TEST000", "SUBMITTED", "Submitted")
                ));

        mockMvc.perform(post("/api/assistant/agent")
                        .contentType(APPLICATION_JSON)
                                                .content("{\"employeeId\":\"EMP-1001\",\"message\":\"Explain policy\"}"))
                .andExpect(status().isOk());
    }
}
