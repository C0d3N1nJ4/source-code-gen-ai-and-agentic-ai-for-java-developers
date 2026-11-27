package com.acme.examples.ch03;

import org.springframework.ai.chat.client.ChatClient;

public final class PromptExample {
    private final ChatClient client;

    public PromptExample(ChatClient client) {
        this.client = client;
    }

    public String ask(String question) {
        return client.prompt().user(question).call().content();
    }
}
