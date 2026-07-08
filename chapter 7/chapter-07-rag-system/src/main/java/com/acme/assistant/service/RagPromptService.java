package com.acme.assistant.service;

import com.acme.assistant.model.RetrievedDocumentChunk;
import com.acme.assistant.prompt.RagPromptTemplate;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class RagPromptService {

    public String buildAugmentedPrompt(String question, List<RetrievedDocumentChunk> chunks) {
        String context = chunks.stream()
                .map(chunk -> {
                    String title = chunk.title() == null ? "Untitled" : chunk.title();
                    String source = chunk.source() == null ? "unknown" : chunk.source();
                    Integer chunkIndex = chunk.chunkIndex() == null ? 0 : chunk.chunkIndex();
                    return "[title=" + title + ", source=" + source + ", chunk=" + chunkIndex + "]\n"
                            + chunk.text();
                })
                .reduce((left, right) -> left + "\n\n" + right)
                .orElse("");

        PromptTemplate template = new PromptTemplate(RagPromptTemplate.TEMPLATE);
        return template.render(Map.of(
                "context", context,
                "question", question));
    }

}
