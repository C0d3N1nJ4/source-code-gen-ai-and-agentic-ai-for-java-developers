package com.acme.assistant.service;

import com.acme.assistant.exception.AssistantException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatClientLlmOrchestrator implements LlmOrchestrator {

    private final ChatClient chatClient;

    public ChatClientLlmOrchestrator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateResponse(String systemMessage, String userMessage) {
        try {
            return chatClient.prompt()
                    .system(systemMessage)
                    .user(userMessage)
                    .call()
                    .content();
        } catch (RuntimeException ex) {
            throw new AssistantException("Failed to generate assistant response", ex);
        }
    }
}
