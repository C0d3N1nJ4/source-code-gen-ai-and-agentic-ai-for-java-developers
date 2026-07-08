package com.acme.assistant.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.assistant.exception.GlobalExceptionHandler;
import com.acme.assistant.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
@Import(GlobalExceptionHandler.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldReturnResponseForValidRequest() throws Exception {
        when(chatService.chat("Explain dependency injection in Spring Boot."))
                .thenReturn("Dependency injection lets Spring provide dependencies automatically.");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"Explain dependency injection in Spring Boot."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());

        verify(chatService).chat("Explain dependency injection in Spring Boot.");
    }

    @Test
    void shouldRejectBlankMessage() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("message")));

        verify(chatService, never()).chat(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldRejectMessageThatIsTooLong() throws Exception {
        String longMessage = "a".repeat(1001);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"" + longMessage + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("at most 1000")));

        verify(chatService, never()).chat(org.mockito.ArgumentMatchers.anyString());
    }
}
