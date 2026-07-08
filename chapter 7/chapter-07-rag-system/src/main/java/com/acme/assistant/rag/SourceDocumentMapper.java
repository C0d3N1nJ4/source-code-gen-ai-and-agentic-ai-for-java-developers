package com.acme.assistant.rag;

import com.acme.assistant.model.RetrievedDocumentChunk;
import com.acme.assistant.model.SourceDocument;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SourceDocumentMapper {

    public List<SourceDocument> map(List<RetrievedDocumentChunk> chunks) {
        Map<String, SourceDocument> deduplicated = new LinkedHashMap<>();
        for (RetrievedDocumentChunk chunk : chunks) {
            String key = (chunk.source() == null ? "unknown" : chunk.source()) + "|"
                    + (chunk.chunkIndex() == null ? 0 : chunk.chunkIndex());

            deduplicated.putIfAbsent(
                    key,
                    new SourceDocument(
                            chunk.source(),
                            chunk.title(),
                            chunk.chunkIndex(),
                            snippet(chunk.text())));
        }
        return deduplicated.values().stream().toList();
    }

    private String snippet(String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= 220) {
            return text;
        }
        return text.substring(0, 220) + "...";
    }

}
