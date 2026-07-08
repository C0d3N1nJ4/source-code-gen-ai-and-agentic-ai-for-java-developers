package com.acme.assistant.document;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.PolicyDocument;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DocumentChunkingService {

    public List<DocumentChunk> chunkDocument(PolicyDocument policyDocument, int chunkSize, int overlap) {
        int safeChunkSize = Math.max(120, chunkSize);
        int safeOverlap = Math.max(0, Math.min(overlap, safeChunkSize / 2));

        List<String> paragraphs = splitParagraphs(policyDocument.content());
        if (paragraphs.isEmpty()) {
            return List.of();
        }

        List<String> chunkTexts = new ArrayList<>();
        String current = "";

        for (String paragraph : paragraphs) {
            if (current.isBlank()) {
                current = paragraph;
                continue;
            }

            String candidate = current + "\n\n" + paragraph;
            if (candidate.length() <= safeChunkSize) {
                current = candidate;
            } else {
                chunkTexts.add(current);
                String overlapPrefix = safeOverlap == 0 ? "" : tail(current, safeOverlap);
                current = overlapPrefix.isBlank() ? paragraph : overlapPrefix + "\n\n" + paragraph;
            }
        }

        if (!current.isBlank()) {
            chunkTexts.add(current);
        }

        List<DocumentChunk> chunks = new ArrayList<>(chunkTexts.size());
        for (int i = 0; i < chunkTexts.size(); i++) {
            Map<String, String> metadata = new LinkedHashMap<>(policyDocument.metadata());
            metadata.put("chunkIndex", String.valueOf(i));
            metadata.put("chunkingStrategy", "paragraph");
            metadata.put("chunkSize", String.valueOf(safeChunkSize));
            metadata.put("overlap", String.valueOf(safeOverlap));

            chunks.add(new DocumentChunk(
                    UUID.randomUUID().toString(),
                    policyDocument.id(),
                    policyDocument.title(),
                    i,
                    chunkTexts.get(i),
                    Map.copyOf(metadata)));
        }

        return chunks;
    }

    private List<String> splitParagraphs(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        String normalized = content.replace("\r\n", "\n").trim();
        String[] parts = normalized.split("\\n\\s*\\n+");

        List<String> paragraphs = new ArrayList<>(parts.length);
        for (String part : parts) {
            String paragraph = part.trim();
            if (!paragraph.isBlank()) {
                paragraphs.add(paragraph);
            }
        }
        return paragraphs;
    }

    private String tail(String value, int overlap) {
        if (value.isBlank() || overlap <= 0) {
            return "";
        }
        int start = Math.max(0, value.length() - overlap);
        return value.substring(start).trim();
    }
}
