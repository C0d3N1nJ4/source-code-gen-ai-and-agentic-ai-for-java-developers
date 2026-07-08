package com.acme.assistant.document;

import com.acme.assistant.model.EnterpriseDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentChunker {

    public List<Document> chunk(EnterpriseDocument source, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than zero");
        }

        List<String> chunkTexts = splitByWords(source.content(), chunkSize);
        List<Document> chunks = new ArrayList<>(chunkTexts.size());
        String sourceDocumentId = UUID.randomUUID().toString();

        for (int i = 0; i < chunkTexts.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceDocumentId", sourceDocumentId);
            metadata.put("source", source.source());
            metadata.put("title", source.title());
            metadata.put("chunkIndex", i + 1);
            metadata.put("chunkCount", chunkTexts.size());
            metadata.put("chunkId", sourceDocumentId + "-" + (i + 1));

            if (source.metadata() != null && !source.metadata().isEmpty()) {
                metadata.putAll(source.metadata());
            }

            chunks.add(new Document(chunkTexts.get(i), metadata));
        }

        return chunks;
    }

    private List<String> splitByWords(String content, int chunkSize) {
        String[] words = content.trim().split("\\s+");
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String word : words) {
            int nextLength = currentChunk.length() == 0
                    ? word.length()
                    : currentChunk.length() + 1 + word.length();

            if (nextLength > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder();
            }

            if (currentChunk.length() > 0) {
                currentChunk.append(' ');
            }
            currentChunk.append(word);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }

}
