package com.acme.examples.ch03;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PromptExampleTest {
    @Test
    void sendsQuestionAndReturnsModelResponse() {
        ChatClient client = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec request = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec response = mock(ChatClient.CallResponseSpec.class);
        when(client.prompt()).thenReturn(request);
        when(request.user("What is Java?")).thenReturn(request);
        when(request.call()).thenReturn(response);
        when(response.content()).thenReturn("A programming language.");

        assertEquals("A programming language.", new PromptExample(client).ask("What is Java?"));
        verify(request).user("What is Java?");
    }
}
