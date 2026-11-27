package com.acme.assistant.ask;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {
    private final ChatClient client;

    public AssistantService(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    public String ask(String question) {
        String answer = client.prompt()
                .system("You are Acme's learning assistant. Do not invent company policies. State uncertainty.")
                .user(question)
                .call()
                .content();
        if (answer == null || answer.isBlank()) {
            throw new IllegalStateException("Model returned no answer");
        }
        return answer;
    }
}
