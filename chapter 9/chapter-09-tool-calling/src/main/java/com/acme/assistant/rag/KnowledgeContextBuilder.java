package com.acme.assistant.rag;

import com.acme.assistant.model.KnowledgeResult;
import com.acme.assistant.model.SourceDocument;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeContextBuilder {

    public String toPromptContext(List<KnowledgeResult> results) {
        if (results == null || results.isEmpty()) {
            return "No enterprise knowledge snippets were retrieved for this request.";
        }

        return results.stream()
                .map(this::formatSnippet)
                .reduce((left, right) -> left + "\n\n" + right)
                .orElse("");
    }

    public List<SourceDocument> toSourceDocuments(List<KnowledgeResult> results) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(result -> new SourceDocument(
                        result.title() == null ? "Untitled" : result.title(),
                        result.source() == null ? "unknown" : result.source(),
                        result.content(),
                        result.metadata() == null ? Map.of() : result.metadata()))
                .toList();
    }

    private String formatSnippet(KnowledgeResult result) {
        String title = result.title() == null ? "Untitled" : result.title();
        String source = result.source() == null ? "unknown" : result.source();
        return "[title=" + title + ", source=" + source + "]\n" + result.content();
    }
}
