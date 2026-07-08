package com.acme.assistant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.model.RetrievedDocumentChunk;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RagPromptServiceTest {

    private final RagPromptService ragPromptService = new RagPromptService();

    @Test
    void buildsPromptUsingTemplateAndContext() {
        List<RetrievedDocumentChunk> chunks = List.of(
                new RetrievedDocumentChunk(
                        "doc-1",
                        "Remote work during international travel requires manager approval.",
                        "remote-work-policy",
                        "Remote Work Policy",
                        1,
                        Map.of("source", "remote-work-policy")));

        String prompt = ragPromptService.buildAugmentedPrompt(
                "Can I work remotely while travelling overseas?",
                chunks);

        assertThat(prompt).contains("Answer ONLY using the supplied context");
        assertThat(prompt).contains("Remote Work Policy");
        assertThat(prompt).contains("remote-work-policy");
        assertThat(prompt).contains("Can I work remotely while travelling overseas?");
        assertThat(prompt).contains("I don't have enough information in the provided context to answer that.");
    }

}
