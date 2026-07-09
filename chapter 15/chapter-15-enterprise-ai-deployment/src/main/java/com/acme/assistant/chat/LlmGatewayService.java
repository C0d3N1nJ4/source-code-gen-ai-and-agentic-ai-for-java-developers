package com.acme.assistant.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmGatewayService {

    private final ChatClient chatClient;

    public LlmGatewayService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String ask(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .content();
    }

    public String fallback(String question) {
        return "Fallback response: I could not reach the primary model for question: " + question;
    }
}
