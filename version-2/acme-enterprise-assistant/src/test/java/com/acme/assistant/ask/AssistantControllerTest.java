package com.acme.assistant.ask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssistantController.class)
class AssistantControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean AssistantService service;

    @Test
    void returnsAnswer() throws Exception {
        when(service.ask("Hello")).thenReturn("Hello from Acme");
        mvc.perform(post("/api/v1/assistant/ask").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Hello from Acme"));
        verify(service).ask("Hello");
    }

    @Test
    void rejectsBlankQuestion() throws Exception {
        mvc.perform(post("/api/v1/assistant/ask").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\" \"}")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void rejectsOversizedQuestion() throws Exception {
        mvc.perform(post("/api/v1/assistant/ask").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"" + "a".repeat(4001) + "\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
