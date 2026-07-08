package com.acme.assistant.service;

import com.acme.assistant.config.AiProperties;
import com.acme.assistant.model.AssistantRequest;
import com.acme.assistant.model.AssistantResponse;
import com.acme.assistant.prompt.AssistantPromptTemplate;
import com.acme.assistant.rag.KnowledgeContextBuilder;
import com.acme.assistant.search.KnowledgeSearchCriteria;
import java.time.Instant;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final ChatClient chatClient;
    private final KnowledgeSearchService knowledgeSearchService;
    private final KnowledgeContextBuilder knowledgeContextBuilder;
        private final AiProperties aiProperties;

    public AssistantService(
            ChatClient chatClient,
            KnowledgeSearchService knowledgeSearchService,
                        KnowledgeContextBuilder knowledgeContextBuilder,
                        AiProperties aiProperties) {
        this.chatClient = chatClient;
        this.knowledgeSearchService = knowledgeSearchService;
        this.knowledgeContextBuilder = knowledgeContextBuilder;
                this.aiProperties = aiProperties;
    }

    public AssistantResponse chat(AssistantRequest request) {
                Integer topK = aiProperties.ragTopK() == null ? 4 : aiProperties.ragTopK();
                var knowledgeResults = knowledgeSearchService.search(new KnowledgeSearchCriteria(request.message(), topK));
        String knowledgeContext = knowledgeContextBuilder.toPromptContext(knowledgeResults);

        PromptTemplate systemTemplate = new PromptTemplate(AssistantPromptTemplate.SYSTEM_PROMPT);
        String systemPrompt = systemTemplate.render(Map.of(
                "knowledgeContext", knowledgeContext,
                "employeeId", request.employeeId() == null ? "UNKNOWN" : request.employeeId()));

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(request.message())
                .call()
                .content();

        return new AssistantResponse(
                request.message(),
                answer,
                knowledgeContextBuilder.toSourceDocuments(knowledgeResults),
                Instant.now());
    }
}
