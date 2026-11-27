package com.acme.assistant.ask;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@Valid @RequestBody AskRequest request) {
        return Flux.defer(() -> service.stream(request.message()))
                .map(token -> ServerSentEvent.builder(token).event("token").build())
                .concatWithValues(ServerSentEvent.builder("complete").event("done").build())
                .onErrorResume(ModelFailure.class, failure -> Flux.just(
                        ServerSentEvent.builder(failure.code()).event("error").build()));
    }

    public record AskRequest(@NotBlank @Size(max = 4000) String message) {}
    public record AskResponse(String answer) {}
}
