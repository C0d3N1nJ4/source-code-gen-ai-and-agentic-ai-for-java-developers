package com.acme.assistant.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class PromptTemplateService {

    public String render(String templateName, Map<String, Object> variables) {
        String templateSource = loadTemplate("prompts/" + templateName);
        return new PromptTemplate(templateSource).render(variables);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load prompt template: " + path, ex);
        }
    }
}
