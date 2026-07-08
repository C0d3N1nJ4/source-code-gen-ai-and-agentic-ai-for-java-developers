package com.acme.assistant.controller;

import com.acme.assistant.model.AssistantChatRequest;
import com.acme.assistant.model.AssistantChatResponse;
import com.acme.assistant.service.AssistantService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/assistant", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AssistantChatResponse chat(@RequestBody @Valid AssistantChatRequest request) {
        return assistantService.chat(request);
    }
}
