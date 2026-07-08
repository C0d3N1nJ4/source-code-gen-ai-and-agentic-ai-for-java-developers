package com.acme.assistant.document;

import com.acme.assistant.model.SourceDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentChunker {

    public List<Document> chunk(SourceDocument sourceDocument, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than zero");
        }

        List<String> chunkTexts = splitByWords(sourceDocument.content(), chunkSize);
        List<Document> chunks = new ArrayList<>(chunkTexts.size());
        String sourceDocumentId = UUID.randomUUID().toString();

        for (int i = 0; i < chunkTexts.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceDocumentId", sourceDocumentId);
            metadata.put("source", sourceDocument.source());
            metadata.put("title", sourceDocument.title());
            metadata.put("chunkIndex", i + 1);
            metadata.put("chunkCount", chunkTexts.size());
            metadata.put("chunkId", sourceDocumentId + "-" + (i + 1));

            if (sourceDocument.metadata() != null && !sourceDocument.metadata().isEmpty()) {
                metadata.putAll(sourceDocument.metadata());
            }

            chunks.add(new Document(chunkTexts.get(i), metadata));
        }

        return chunks;
    }

    private List<String> splitByWords(String text, int chunkSize) {
        String[] words = text.trim().split("\\s+");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            int nextLength = current.isEmpty() ? word.length() : current.length() + 1 + word.length();
            if (nextLength > chunkSize && !current.isEmpty()) {
                chunks.add(current.toString());
                current = new StringBuilder();
            }

            if (!current.isEmpty()) {
                current.append(' ');
            }
            current.append(word);
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString());
        }

        return chunks;
    }
}
