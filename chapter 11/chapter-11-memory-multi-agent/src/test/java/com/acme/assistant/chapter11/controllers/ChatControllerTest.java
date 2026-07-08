package com.acme.assistant.chapter11.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.assistant.chapter11.dto.ChatResponse;
import com.acme.assistant.chapter11.services.EnterpriseAssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnterpriseAssistantService enterpriseAssistantService;

    @Test
    void shouldHandleChatRequest() throws Exception {
        org.mockito.Mockito.when(enterpriseAssistantService.chat(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new ChatResponse("conv-1", "response", java.util.List.of("FINANCE"), 10, 40, 20));

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"EMP-1\",\"message\":\"Need travel estimate\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.response").value("response"));
    }
}
