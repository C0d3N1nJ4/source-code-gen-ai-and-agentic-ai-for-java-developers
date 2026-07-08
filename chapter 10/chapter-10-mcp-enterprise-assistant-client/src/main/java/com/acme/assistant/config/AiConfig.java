package com.acme.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatModel chatModel, SyncMcpToolCallbackProvider mcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(mcpToolCallbackProvider.getToolCallbacks())
                .build();
    }
}
