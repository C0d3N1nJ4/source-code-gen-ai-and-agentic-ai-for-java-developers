package com.acme.assistant.ask;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import reactor.core.publisher.Flux;
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
    @Test
    void mapsTimeoutToSafeProblem() throws Exception {
        when(service.ask("Hello")).thenThrow(new ModelFailure("timeout"));
        mvc.perform(post("/api/v1/assistant/ask").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"Hello\"}"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.code").value("timeout"))
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void streamsTokensAndCompletion() throws Exception {
        when(service.stream("Hello")).thenReturn(Flux.just("one", "two"));
        var result = mvc.perform(post("/api/v1/assistant/stream")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_EVENT_STREAM)
                .content("{\"message\":\"Hello\"}"))
                .andExpect(request().asyncStarted()).andReturn();
        mvc.perform(asyncDispatch(result)).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("event:done")));
    }
}
