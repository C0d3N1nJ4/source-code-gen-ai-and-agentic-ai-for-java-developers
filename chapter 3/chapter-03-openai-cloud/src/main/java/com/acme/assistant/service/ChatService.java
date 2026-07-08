package com.acme.assistant.service;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.exception.AiProviderException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatModel chatModel;
    private final AssistantProperties assistantProperties;

    public ChatService(ChatModel chatModel, AssistantProperties assistantProperties) {
        this.chatModel = chatModel;
        this.assistantProperties = assistantProperties;
    }

    public String chat(String message) {
        String promptText = "%s%n%nUser question:%n%s".formatted(
                assistantProperties.getSystemMessage(),
                message);

        try {
            ChatResponse response = chatModel.call(new Prompt(promptText));
            String responseText = response.getResult().getOutput().getText();

            if (responseText == null || responseText.isBlank()) {
                throw new AiProviderException("AI provider returned an empty response");
            }

            return responseText;
        } catch (AiProviderException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new AiProviderException("Failed to get response from AI provider", ex);
        }
    }
}
