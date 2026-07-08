package com.acme.assistant.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingModelInfoService {

    private final String modelName;

    public EmbeddingModelInfoService(
            @Value("${spring.ai.ollama.embedding.options.model:nomic-embed-text}") String modelName) {
        this.modelName = modelName;
    }

    public String modelName() {
        return modelName;
    }
}
