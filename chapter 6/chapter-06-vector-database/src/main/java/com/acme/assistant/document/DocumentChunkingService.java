package com.acme.assistant.document;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.DocumentMetadata;
import com.acme.assistant.model.PolicyDocumentPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DocumentChunkingService {

    public List<DocumentChunk> chunkDocument(PolicyDocumentPayload document, int chunkSize, int overlap) {
        int safeChunkSize = Math.max(180, chunkSize);
        int safeOverlap = Math.max(0, Math.min(overlap, safeChunkSize / 3));

        List<String> paragraphs = splitParagraphs(document.content());
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
                String overlapText = safeOverlap == 0 ? "" : tail(current, safeOverlap);
                current = overlapText.isBlank() ? paragraph : overlapText + "\n\n" + paragraph;
            }
        }

        if (!current.isBlank()) {
            chunkTexts.add(current);
        }

        String documentId = (document.documentId() == null || document.documentId().isBlank())
                ? slugify(document.title())
                : document.documentId();

        DocumentMetadata metadata = new DocumentMetadata(
                document.title(),
                document.department(),
                document.category(),
                document.source());

        List<DocumentChunk> chunks = new ArrayList<>(chunkTexts.size());
        for (int i = 0; i < chunkTexts.size(); i++) {
            chunks.add(new DocumentChunk(
                    UUID.randomUUID().toString(),
                    documentId,
                    i,
                    chunkTexts.get(i),
                    metadata));
        }

        return chunks;
    }

    private List<String> splitParagraphs(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        String normalized = content.replace("\r\n", "\n").trim();
        String[] raw = normalized.split("\\n\\s*\\n+");

        List<String> paragraphs = new ArrayList<>(raw.length);
        for (String value : raw) {
            String paragraph = value.trim();
            if (!paragraph.isBlank()) {
                paragraphs.add(paragraph);
            }
        }
        return paragraphs;
    }

    private String tail(String value, int overlap) {
        int start = Math.max(0, value.length() - overlap);
        return value.substring(start).trim();
    }

    private String slugify(String value) {
        return value.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
