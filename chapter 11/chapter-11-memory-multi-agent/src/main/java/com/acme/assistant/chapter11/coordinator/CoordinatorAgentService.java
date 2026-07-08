package com.acme.assistant.chapter11.coordinator;

import com.acme.assistant.chapter11.agents.AgentExecutionContext;
import com.acme.assistant.chapter11.agents.AgentResult;
import com.acme.assistant.chapter11.agents.SpecialistAgent;
import com.acme.assistant.chapter11.advisors.PromptTelemetryEstimator;
import com.acme.assistant.chapter11.config.AiPlatformProperties;
import com.acme.assistant.chapter11.rag.EnterpriseKnowledgeService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Coordinator agent responsible for planning, routing, specialist orchestration, and synthesis.
 */
@Service
public class CoordinatorAgentService {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorAgentService.class);

    private final List<SpecialistAgent> specialistAgents;
    private final EnterpriseKnowledgeService enterpriseKnowledgeService;
    private final AiPlatformProperties properties;
    private final PromptTelemetryEstimator telemetryEstimator;
    private final Executor agentExecutionExecutor;
    private final ChatModel chatModel;

    public CoordinatorAgentService(
            List<SpecialistAgent> specialistAgents,
            EnterpriseKnowledgeService enterpriseKnowledgeService,
            AiPlatformProperties properties,
            PromptTelemetryEstimator telemetryEstimator,
            @Qualifier("agentExecutionExecutor") Executor agentExecutionExecutor,
            ChatModel chatModel) {
        this.specialistAgents = specialistAgents;
        this.enterpriseKnowledgeService = enterpriseKnowledgeService;
        this.properties = properties;
        this.telemetryEstimator = telemetryEstimator;
        this.agentExecutionExecutor = agentExecutionExecutor;
        this.chatModel = chatModel;
    }

    public CoordinationResult coordinate(String userId, String conversationId, String request,
                                         List<String> shortTermContext, List<String> longTermMemory) {
        Instant start = Instant.now();
        List<String> ragSnippets = enterpriseKnowledgeService.retrieve(request, properties.semanticTopK());

        AgentExecutionContext context = new AgentExecutionContext(
                userId,
                conversationId,
                request,
                shortTermContext,
                longTermMemory,
                ragSnippets);

        List<CompletableFuture<AgentResult>> tasks = specialistAgents.stream()
                .filter(agent -> agent.supports(request))
                .map(agent -> CompletableFuture.supplyAsync(() -> agent.execute(context), agentExecutionExecutor)
                        .exceptionally(ex -> new AgentResult(agent.type(), "Agent failure handled by coordinator.",
                                java.util.Map.of("error", ex.getMessage()))))
                .toList();

        List<AgentResult> results = tasks.stream().map(CompletableFuture::join).toList();
        if (results.isEmpty()) {
            results = List.of(new AgentResult(null, "No specialist selected; coordinator answered directly.", java.util.Map.of()));
        }

        String synthesisPrompt = buildSynthesisPrompt(request, shortTermContext, longTermMemory, ragSnippets, results);
        int promptTokens = telemetryEstimator.estimateTokens(synthesisPrompt);
        String response;
        try {
            response = chatModel.call(synthesisPrompt);
        } catch (RuntimeException ex) {
            response = fallbackSummary(results);
        }

        Duration elapsed = Duration.between(start, Instant.now());
        log.info("Coordinator completed. conversationId={}, selectedAgents={}, promptTokens={}, responseTimeMs={}",
                conversationId,
                results.stream().map(result -> String.valueOf(result.agentType())).toList(),
                promptTokens,
                elapsed.toMillis());

        return new CoordinationResult(response, results, elapsed.toMillis(), promptTokens, telemetryEstimator.estimateTokens(response));
    }

    private String buildSynthesisPrompt(String request, List<String> shortTermContext,
                                        List<String> longTermMemory, List<String> ragSnippets,
                                        List<AgentResult> specialistResults) {
        return properties.coordinatorSystemPrompt()
                + "\nUser request: " + request
                + "\nRecent conversation context: " + shortTermContext
                + "\nLong-term memory: " + longTermMemory
                + "\nRetrieved enterprise documents: " + ragSnippets
                + "\nSpecialist outputs: " + specialistResults
                + "\nProduce one unified enterprise response for the employee.";
    }

    private String fallbackSummary(List<AgentResult> results) {
        List<String> summaries = new ArrayList<>();
        for (AgentResult result : results) {
            summaries.add(result.summary());
        }
        return String.join(" ", summaries);
    }

    /**
     * Immutable coordination output consumed by chat service.
     */
    public record CoordinationResult(
            String response,
            List<AgentResult> agentResults,
            long responseTimeMs,
            int promptTokens,
            int responseTokens
    ) {
    }
}
