package com.acme.assistant.ask;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssistantServiceTest {
    private final ChatClient.Builder builder = mock(ChatClient.Builder.class);
    private final ChatClient client = mock(ChatClient.class, RETURNS_DEEP_STUBS);

    private AssistantService service(Flux<String> tokens) {
        when(builder.build()).thenReturn(client);
        when(client.prompt().system(anyString()).user(anyString()).stream().content()).thenReturn(tokens);
        return new AssistantService(builder, Duration.ofMillis(200), new SimpleMeterRegistry());
    }

    @Test void joinsTokens() {
        assertEquals("Hello world", service(Flux.just("Hello ", "world")).ask("Hi"));
    }

    @Test void mapsEmptyResponse() {
        assertEquals("empty_response", assertThrows(ModelFailure.class, () -> service(Flux.empty()).ask("Hi")).code());
    }

    @Test void sanitizesProviderErrors() {
        assertEquals("provider_unavailable", assertThrows(ModelFailure.class,
                () -> service(Flux.error(new IllegalStateException("secret"))).ask("Hi")).code());
    }

    @Test void boundsRequest() {
        assertEquals("timeout", assertThrows(ModelFailure.class, () -> service(Flux.never()).ask("Hi")).code());
    }

    @Test void streamsTokens() {
        StepVerifier.create(service(Flux.just("one", "two")).stream("Hi"))
                .expectNext("one", "two").verifyComplete();
    }
}
