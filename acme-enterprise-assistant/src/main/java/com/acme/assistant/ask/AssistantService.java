package com.acme.assistant.ask;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class AssistantService {
    private static final String SYSTEM = "You are Acme's learning assistant. Do not invent company policies. State uncertainty.";
    private final ChatClient client;
    private final Duration timeout;
    private final MeterRegistry metrics;

    public AssistantService(ChatClient.Builder builder, @Value("${acme.model.timeout:60s}") Duration timeout,
                            MeterRegistry metrics) {
        this.client = builder.build();
        this.timeout = timeout;
        this.metrics = metrics;
    }

    public String ask(String question) {
        var timer = io.micrometer.core.instrument.Timer.start(metrics);
        try {
            String answer = client.prompt().system(SYSTEM).user(question).stream().content()
                    .reduce("", String::concat).timeout(timeout).block();
            if (answer == null || answer.isBlank()) {
                throw new ModelFailure("empty_response");
            }
            metrics.counter("acme.assistant.requests", "outcome", "success").increment();
            return answer;
        } catch (RuntimeException ex) {
            ModelFailure failure = classify(ex);
            metrics.counter("acme.assistant.requests", "outcome", failure.code()).increment();
            throw failure;
        } finally {
            timer.stop(metrics.timer("acme.assistant.duration"));
        }
    }

    public Flux<String> stream(String question) {
        return Flux.defer(() -> client.prompt().system(SYSTEM).user(question).stream().content())
                .takeUntilOther(reactor.core.publisher.Mono.delay(timeout)
                        .flatMap(ignored -> reactor.core.publisher.Mono.error(new ModelFailure("timeout"))))
                .timeout(timeout)
                .onErrorMap(AssistantService::classify);
    }

    private static ModelFailure classify(Throwable error) {
        if (error instanceof ModelFailure failure) return failure;
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof TimeoutException || cause instanceof java.net.SocketTimeoutException
                    || cause instanceof java.net.http.HttpTimeoutException) return new ModelFailure("timeout");
            if (cause.getCause() == cause) break;
        }
        return new ModelFailure("provider_unavailable");
    }
}
