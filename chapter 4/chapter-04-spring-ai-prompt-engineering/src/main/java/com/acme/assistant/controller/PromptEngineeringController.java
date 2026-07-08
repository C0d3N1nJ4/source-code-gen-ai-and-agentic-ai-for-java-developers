package com.acme.assistant.controller;

import com.acme.assistant.model.ConceptExplanationRequest;
import com.acme.assistant.model.ConceptExplanationResponse;
import com.acme.assistant.model.DocumentSummaryRequest;
import com.acme.assistant.model.DocumentSummaryResponse;
import com.acme.assistant.model.PromptSafetyAssessment;
import com.acme.assistant.model.PromptSafetyRequest;
import com.acme.assistant.model.TicketClassificationRequest;
import com.acme.assistant.model.TicketClassificationResponse;
import com.acme.assistant.service.PromptEngineeringService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompts")
public class PromptEngineeringController {

    private final PromptEngineeringService promptEngineeringService;

    public PromptEngineeringController(PromptEngineeringService promptEngineeringService) {
        this.promptEngineeringService = promptEngineeringService;
    }

    @PostMapping("/explain")
    public ResponseEntity<ConceptExplanationResponse> explain(
            @Valid @RequestBody ConceptExplanationRequest request) {
        return ResponseEntity.ok(promptEngineeringService.explainConcept(request));
    }

    @PostMapping("/summarize")
    public ResponseEntity<DocumentSummaryResponse> summarize(
            @Valid @RequestBody DocumentSummaryRequest request) {
        return ResponseEntity.ok(promptEngineeringService.summarizeDocument(request));
    }

    @PostMapping("/classify-ticket")
    public ResponseEntity<TicketClassificationResponse> classifyTicket(
            @Valid @RequestBody TicketClassificationRequest request) {
        return ResponseEntity.ok(promptEngineeringService.classifyTicket(request));
    }

    @PostMapping("/safety-check")
    public ResponseEntity<PromptSafetyAssessment> safetyCheck(
            @Valid @RequestBody PromptSafetyRequest request) {
        return ResponseEntity.ok(promptEngineeringService.safetyCheck(request));
    }
}
