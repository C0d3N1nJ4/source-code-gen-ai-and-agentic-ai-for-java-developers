package com.acme.assistant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.assistant.model.RagResponse;
import com.acme.assistant.model.RetrievedDocumentChunk;
import com.acme.assistant.model.SourceDocument;
import com.acme.assistant.rag.SourceDocumentMapper;
import com.acme.assistant.search.KnowledgeSearchCriteria;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.model.ChatModel;

class RagServiceTest {

    private KnowledgeSearchService knowledgeSearchService;
    private RagPromptService ragPromptService;
    private ChatModel chatModel;
    private SourceDocumentMapper sourceDocumentMapper;
    private RagService ragService;

    @BeforeEach
    void setUp() {
        knowledgeSearchService = Mockito.mock(KnowledgeSearchService.class);
        ragPromptService = Mockito.mock(RagPromptService.class);
        chatModel = Mockito.mock(ChatModel.class);
        sourceDocumentMapper = Mockito.mock(SourceDocumentMapper.class);
        ragService = new RagService(knowledgeSearchService, ragPromptService, chatModel, sourceDocumentMapper);
    }

    @Test
    void returnsGroundedResponseWithSourcesWhenContextExists() {
        List<RetrievedDocumentChunk> chunks = List.of(new RetrievedDocumentChunk(
                "chunk-1",
                "International expense claims are submitted through the finance portal.",
                "expense-claim-policy",
                "Expense Claim Policy",
                1,
                Map.of()));
        List<SourceDocument> mappedSources = List.of(new SourceDocument(
                "expense-claim-policy",
                "Expense Claim Policy",
                1,
                "International expense claims are submitted through the finance portal."));

        when(knowledgeSearchService.search(any(KnowledgeSearchCriteria.class))).thenReturn(chunks);
        when(ragPromptService.buildAugmentedPrompt(eq("How do I submit an international expense claim?"), eq(chunks)))
                .thenReturn("augmented prompt");
        when(chatModel.call("augmented prompt")).thenReturn("Submit it through the finance portal within 10 business days.");
        when(sourceDocumentMapper.map(chunks)).thenReturn(mappedSources);

        RagResponse response = ragService.ask("How do I submit an international expense claim?");

        assertThat(response.answer()).contains("finance portal");
        assertThat(response.sources()).hasSize(1);
        verify(chatModel).call("augmented prompt");
    }

    @Test
    void returnsFallbackWhenNoRelevantDocumentsFound() {
        when(knowledgeSearchService.search(any(KnowledgeSearchCriteria.class))).thenReturn(List.of());

        RagResponse response = ragService.ask("Question with no matching context");

        assertThat(response.answer())
                .isEqualTo("I don't have enough information in the provided context to answer that.");
        assertThat(response.sources()).isEmpty();
        verify(chatModel, never()).call(any(String.class));
    }

}
