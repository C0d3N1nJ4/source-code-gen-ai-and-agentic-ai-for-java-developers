package com.acme.assistant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.exception.AiProviderException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatModel chatModel;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        AssistantProperties assistantProperties = new AssistantProperties();
        assistantProperties.setSystemMessage("You are the Acme Corporation AI assistant.");
        chatService = new ChatService(chatModel, assistantProperties);
    }

    @Test
    void shouldExtractResponseTextFromChatResponse() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("Spring injects dependencies for you."));

        String result = chatService.chat("Explain dependency injection in Spring Boot.");

        assertEquals("Spring injects dependencies for you.", result);
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    void shouldWrapProviderFailure() {
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("provider down"));

        assertThrows(AiProviderException.class,
                () -> chatService.chat("Explain dependency injection in Spring Boot."));
    }

    private ChatResponse chatResponse(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }
}
