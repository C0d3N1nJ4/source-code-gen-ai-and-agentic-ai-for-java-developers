package com.acme.assistant.controller;

import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.service.AssistantService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssistantController.class)
class AssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService assistantService;

    @Test
    void shouldReturnAssistantResponse() throws Exception {
        when(assistantService.chat(any())).thenReturn(new AssistantResponse(
                "How many leave days do I have remaining?",
                "You currently have 14 days remaining.",
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z")));

        mockMvc.perform(post("/api/assistant/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "How many leave days do I have remaining?",
                                  "employeeId": "EMP-1001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("You currently have 14 days remaining."));
    }
}
