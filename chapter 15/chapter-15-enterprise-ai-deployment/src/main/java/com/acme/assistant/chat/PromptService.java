package com.acme.assistant.chat;

import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    private final String systemPrompt;

    public PromptService(ResourceLoader resourceLoader) {
        this.systemPrompt = loadPrompt(resourceLoader);
    }

    public String buildSystemPrompt(String model, String userId) {
        return systemPrompt + "\nActive model: " + model + "\nUser: " + userId;
    }

    public String buildUserPrompt(String question, Map<String, Object> context, String ragContext) {
        return "Question: " + question + "\nContext: " + context + "\nRAG: " + ragContext;
    }

    private String loadPrompt(ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource("classpath:prompts/system-assistant.txt");
            return new String(resource.getInputStream().readAllBytes());
        } catch (Exception ex) {
            return "You are the Acme assistant.";
        }
    }
}
