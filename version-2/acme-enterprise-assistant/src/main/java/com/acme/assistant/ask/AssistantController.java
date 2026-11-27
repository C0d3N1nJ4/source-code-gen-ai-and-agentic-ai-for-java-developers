package com.acme.assistant.ask;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {
    private final AssistantService service;

    public AssistantController(AssistantService service) {
        this.service = service;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return new AskResponse(service.ask(request.message()));
    }

    public record AskRequest(@NotBlank @Size(max = 4000) String message) {}
    public record AskResponse(String answer) {}
}
