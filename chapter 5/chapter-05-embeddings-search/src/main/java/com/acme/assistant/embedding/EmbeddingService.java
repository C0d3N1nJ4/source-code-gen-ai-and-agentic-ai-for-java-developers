package com.acme.assistant.embedding;

import com.acme.assistant.exception.EmbeddingOperationException;
import com.acme.assistant.model.DocumentChunk;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final String embeddingModelName;

    public EmbeddingService(
            EmbeddingModel embeddingModel,
            @Value("${spring.ai.ollama.embedding.options.model:nomic-embed-text}") String embeddingModelName) {
        this.embeddingModel = embeddingModel;
        this.embeddingModelName = embeddingModelName;
    }

    public float[] embedDocumentChunk(DocumentChunk chunk) {
        return embed(chunk.content());
    }

    public float[] embedUserQuery(String query) {
        return embed(query);
    }

    public String embeddingModelName() {
        return embeddingModelName;
    }

    private float[] embed(String text) {
        try {
            return embeddingModel.embed(text);
        } catch (Exception ex) {
            throw new EmbeddingOperationException("Embedding provider unavailable", ex);
        }
    }
}
