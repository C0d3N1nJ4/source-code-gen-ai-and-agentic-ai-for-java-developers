package com.acme.assistant.service;

import com.acme.assistant.model.RagResponse;
import com.acme.assistant.model.RetrievedDocumentChunk;
import com.acme.assistant.rag.SourceDocumentMapper;
import com.acme.assistant.search.KnowledgeSearchCriteria;
import java.util.List;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private static final String NO_CONTEXT_ANSWER =
            "I don't have enough information in the provided context to answer that.";

    private final KnowledgeSearchService knowledgeSearchService;
    private final RagPromptService ragPromptService;
    private final ChatModel chatModel;
    private final SourceDocumentMapper sourceDocumentMapper;

    public RagService(
            KnowledgeSearchService knowledgeSearchService,
            RagPromptService ragPromptService,
            ChatModel chatModel,
            SourceDocumentMapper sourceDocumentMapper) {
        this.knowledgeSearchService = knowledgeSearchService;
        this.ragPromptService = ragPromptService;
        this.chatModel = chatModel;
        this.sourceDocumentMapper = sourceDocumentMapper;
    }

    public RagResponse ask(String question) {
        List<RetrievedDocumentChunk> chunks = knowledgeSearchService.search(new KnowledgeSearchCriteria(question, null));

        if (chunks.isEmpty()) {
            return new RagResponse(question, NO_CONTEXT_ANSWER, List.of());
        }

        String augmentedPrompt = ragPromptService.buildAugmentedPrompt(question, chunks);
        String answer = chatModel.call(augmentedPrompt);

        return new RagResponse(question, answer, sourceDocumentMapper.map(chunks));
    }

}
