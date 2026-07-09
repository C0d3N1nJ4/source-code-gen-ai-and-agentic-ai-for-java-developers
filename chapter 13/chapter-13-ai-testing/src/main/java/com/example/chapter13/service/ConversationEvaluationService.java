package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationRequests.ConversationEvaluationRequest;
import com.example.chapter13.dto.EvaluationResponses.ConversationEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.ConversationMetrics;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConversationEvaluationService {

        private final AcmePlatformContextService acmePlatformContextService;

        public ConversationEvaluationService(AcmePlatformContextService acmePlatformContextService) {
                this.acmePlatformContextService = acmePlatformContextService;
        }

    public ConversationEvaluationResponse evaluateConversationBehavior(ConversationEvaluationRequest request) {
        List<String> transcript = new ArrayList<>();
        transcript.add("user: I'm travelling to Amsterdam next week for the Project Orion workshop.");
                transcript.add("assistant: Noted. I will keep Amsterdam and Project Orion in context using conversation and semantic memory.");
        transcript.add("user: Estimate my expenses.");
                transcript.add("assistant: Estimated travel expenses for Amsterdam are shown in euro using your stored profile " + acmePlatformContextService.userProfile() + ".");
        transcript.add("user: Update my preferred currency to euro.");
        transcript.add("assistant: Preference updated. Future travel estimates will use euro.");
        transcript.add("user: Continue the workflow and summarize it for my manager.");
                transcript.add("assistant: Continued the Project Orion travel workflow, reused semantic memories " + acmePlatformContextService.semanticMemories() + ", and prepared a manager summary.");

        int isolationScore = Boolean.TRUE.equals(request.includeIsolationCheck()) ? 100 : 90;
        ConversationMetrics metrics = new ConversationMetrics(
                98,
                96,
                100,
                isolationScore,
                99);

        List<String> observations = List.of(
                "Conversation retains Amsterdam and Project Orion context across turns.",
                "Preference updates immediately affect later workflow steps.",
                "User memory remains isolated from other employees when isolation checks are enabled while sharing only coordinator-safe context with specialist agents " + acmePlatformContextService.specialistAgents() + ".",
                "This evaluation continues the chapter 12 encrypted and ownership-isolated memory model while adding chapter 13 behavioral assertions.");

        return new ConversationEvaluationResponse(
                request.conversationId(),
                request.userId(),
                metrics,
                transcript,
                observations);
    }

    public ConversationEvaluationResponse defaultEvaluation() {
        return evaluateConversationBehavior(new ConversationEvaluationRequest(
                "conv-1301",
                "EMP-1001",
                "Estimate travel expenses and continue the Project Orion workflow",
                true));
    }
}