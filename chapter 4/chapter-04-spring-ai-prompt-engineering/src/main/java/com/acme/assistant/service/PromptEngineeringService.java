package com.acme.assistant.service;

import com.acme.assistant.exception.InvalidAiResponseException;
import com.acme.assistant.model.ConceptExplanationRequest;
import com.acme.assistant.model.ConceptExplanationResponse;
import com.acme.assistant.model.DocumentSummaryRequest;
import com.acme.assistant.model.DocumentSummaryResponse;
import com.acme.assistant.model.PromptSafetyAssessment;
import com.acme.assistant.model.PromptSafetyRequest;
import com.acme.assistant.model.TicketClassificationRequest;
import com.acme.assistant.model.TicketClassificationResponse;
import com.acme.assistant.moderation.PromptInjectionHeuristicService;
import com.acme.assistant.prompt.SystemPromptFactory;
import com.acme.assistant.template.PromptTemplateService;
import com.acme.assistant.validation.AiResponseValidationService;
import com.acme.assistant.validation.JsonResponseExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    private final ChatModel chatModel;
    private final PromptTemplateService promptTemplateService;
    private final SystemPromptFactory systemPromptFactory;
    private final ObjectMapper objectMapper;
    private final JsonResponseExtractor jsonResponseExtractor;
    private final AiResponseValidationService aiResponseValidationService;
    private final PromptInjectionHeuristicService promptInjectionHeuristicService;

    public PromptEngineeringService(
            ChatModel chatModel,
            PromptTemplateService promptTemplateService,
            SystemPromptFactory systemPromptFactory,
            ObjectMapper objectMapper,
            JsonResponseExtractor jsonResponseExtractor,
            AiResponseValidationService aiResponseValidationService,
            PromptInjectionHeuristicService promptInjectionHeuristicService) {
        this.chatModel = chatModel;
        this.promptTemplateService = promptTemplateService;
        this.systemPromptFactory = systemPromptFactory;
        this.objectMapper = objectMapper;
        this.jsonResponseExtractor = jsonResponseExtractor;
        this.aiResponseValidationService = aiResponseValidationService;
        this.promptInjectionHeuristicService = promptInjectionHeuristicService;
    }

    public ConceptExplanationResponse explainConcept(ConceptExplanationRequest request) {
        promptInjectionHeuristicService.rejectIfUnsafe(request.topic());
        promptInjectionHeuristicService.rejectIfUnsafe(request.audience());

        String userPrompt = promptTemplateService.render("explain-java-concept.st", Map.of(
                "topic", request.topic(),
                "audience", request.audience(),
                "format", request.format()));

        String explanation = callModel(systemPromptFactory.explanationSystemPrompt(), userPrompt);

        ConceptExplanationResponse response = new ConceptExplanationResponse(
                request.topic(),
                request.audience(),
                request.format(),
                explanation);

        return aiResponseValidationService.validateOrThrow(response, "concept explanation");
    }

    public DocumentSummaryResponse summarizeDocument(DocumentSummaryRequest request) {
        promptInjectionHeuristicService.rejectIfUnsafe(request.documentText());

        String userPrompt = promptTemplateService.render("summarize-document.st", Map.of(
                "documentText", request.documentText(),
                "format", request.format()));

        String rawResponse = callModel(systemPromptFactory.summarySystemPrompt(), userPrompt);
        return parseAndValidateJsonResponse(rawResponse, DocumentSummaryResponse.class, "document summary");
    }

    public TicketClassificationResponse classifyTicket(TicketClassificationRequest request) {
        promptInjectionHeuristicService.rejectIfUnsafe(request.ticketText());

        String userPrompt = promptTemplateService.render("classify-support-ticket.st", Map.of(
                "ticketText", request.ticketText(),
                "format", request.format()));

        String rawResponse = callModel(systemPromptFactory.ticketClassificationSystemPrompt(), userPrompt);
        return parseAndValidateJsonResponse(rawResponse, TicketClassificationResponse.class, "ticket classification");
    }

    public PromptSafetyAssessment safetyCheck(PromptSafetyRequest request) {
        PromptSafetyAssessment heuristicAssessment = promptInjectionHeuristicService.assess(request.inputText());

        String userPrompt = promptTemplateService.render("safety-check.st", Map.of("inputText", request.inputText()));

        String rawResponse = callModel(systemPromptFactory.safetySystemPrompt(), userPrompt);
        PromptSafetyAssessment modelAssessment = parseAndValidateJsonResponse(
                rawResponse,
                PromptSafetyAssessment.class,
                "safety assessment");

        PromptSafetyAssessment mergedAssessment = new PromptSafetyAssessment(
                heuristicAssessment.safe() && modelAssessment.safe(),
                heuristicAssessment.safe() ? modelAssessment.reason() : heuristicAssessment.reason(),
                heuristicAssessment.safe() ? modelAssessment.detectedRisk() : heuristicAssessment.detectedRisk(),
                heuristicAssessment.safe()
                        ? modelAssessment.recommendedAction()
                        : "Reject request and escalate according to incident playbook");

        return aiResponseValidationService.validateOrThrow(mergedAssessment, "safety assessment");
    }

    private String callModel(String systemInstruction, String userPromptText) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemInstruction),
                new UserMessage(userPromptText)));

        String responseText = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        if (responseText == null || responseText.isBlank()) {
            throw new InvalidAiResponseException("Model returned an empty response");
        }

        return responseText;
    }

    private <T> T parseAndValidateJsonResponse(String rawResponse, Class<T> targetType, String context) {
        String json = jsonResponseExtractor.extract(rawResponse);
        try {
            T parsed = objectMapper.readValue(json, targetType);
            return aiResponseValidationService.validateOrThrow(parsed, context);
        } catch (InvalidAiResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidAiResponseException("Failed to parse " + context + " response", ex);
        }
    }
}
