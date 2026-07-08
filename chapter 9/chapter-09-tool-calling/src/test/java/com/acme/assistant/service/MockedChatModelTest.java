package com.acme.assistant.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MockedChatModelTest {

    @Test
    void shouldReturnMockedModelResponse() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(anyString())).thenReturn("mocked response");

        String response = chatModel.call("test prompt");

        assertThat(response).isEqualTo("mocked response");
        verify(chatModel).call("test prompt");
    }
}
