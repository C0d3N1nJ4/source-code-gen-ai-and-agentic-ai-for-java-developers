package com.example.chapter13.service;

import com.example.chapter13.dto.EvaluationRequests.RagEvaluationRequest;
import com.example.chapter13.dto.EvaluationResponses.RagEvaluationResponse;
import com.example.chapter13.dto.EvaluationResponses.RetrievalMetrics;
import com.example.chapter13.dto.EvaluationResponses.RetrievedDocument;
import com.example.chapter13.model.EnterpriseKnowledgeDocument;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RagEvaluationService {

    private final AcmePlatformContextService acmePlatformContextService;

    public RagEvaluationService(AcmePlatformContextService acmePlatformContextService) {
        this.acmePlatformContextService = acmePlatformContextService;
    }

    public RagEvaluationResponse evaluateRetrieval(RagEvaluationRequest request) {
        long start = System.nanoTime();
        Set<String> queryTokens = tokenize(request.question());
        String expectedDomain = inferExpectedDomain(queryTokens);
        List<EnterpriseKnowledgeDocument> documents = acmePlatformContextService.knowledgeDocuments();

        List<RetrievedDocument> scoredDocuments = documents.stream()
                .map(document -> new RetrievedDocument(
                        document.documentId(),
                        document.domain(),
                        document.title(),
                        document.excerpt(),
                        overlapScore(queryTokens, document.keywords())))
                .sorted(Comparator.comparingInt(RetrievedDocument::score).reversed())
                .toList();

        int bestScore = scoredDocuments.isEmpty() ? 0 : scoredDocuments.getFirst().score();
        int relevanceThreshold = Math.max(1, bestScore);

        List<RetrievedDocument> retrievedDocuments = scoredDocuments.stream()
                .filter(document -> document.score() >= relevanceThreshold)
                .limit(request.maxDocuments())
                .toList();

        if (retrievedDocuments.isEmpty()) {
            retrievedDocuments = scoredDocuments.stream().limit(request.maxDocuments()).toList();
        }

        long latencyMs = Math.max(4L, (System.nanoTime() - start) / 1_000_000L);
        long domainMatches = retrievedDocuments.stream()
                .filter(document -> document.domain().equalsIgnoreCase(expectedDomain))
                .count();
        int topScore = retrievedDocuments.isEmpty() ? 0 : retrievedDocuments.getFirst().score();
        int relevance = topScore >= 3 ? 96 : clamp(retrievedDocuments.isEmpty() ? 0 : 78 + topScore * 5);
        int precision = clamp((int) ((domainMatches * 100) / Math.max(1, retrievedDocuments.size())));
        long availableRelevant = documents.stream().filter(document -> document.domain().equalsIgnoreCase(expectedDomain)).count();
        int recall = clamp((int) ((domainMatches * 100) / Math.max(1, availableRelevant)));
        int rankingQuality = clamp(!retrievedDocuments.isEmpty() && retrievedDocuments.getFirst().domain().equalsIgnoreCase(expectedDomain)
                ? 100
                : 72);
        int groundedness = clamp(relevance >= 90 && precision >= 66 ? 99 : 84);

        String generatedAnswer = retrievedDocuments.isEmpty()
                ? "No supporting enterprise documents were retrieved for this question."
                : retrievedDocuments.getFirst().excerpt();

        List<String> findings = List.of(
                "Retrieval is evaluated independently from final response generation.",
                "The evaluation corpus reuses chapter 12 MCP resources and chapter 11 business travel context.",
                "Use this report after document, chunking, or embedding changes to catch regressions early.");

        return new RagEvaluationResponse(
                request.question(),
                expectedDomain,
                retrievedDocuments,
                new RetrievalMetrics(relevance, precision, recall, rankingQuality, groundedness, latencyMs),
                generatedAnswer,
                findings);
    }

    public RagEvaluationResponse defaultEvaluation() {
        return evaluateRetrieval(new RagEvaluationRequest(
                "What is Acme Corporation travel reimbursement policy?",
                3));
    }

    private String inferExpectedDomain(Set<String> queryTokens) {
        Map<String, List<String>> domainKeywords = new LinkedHashMap<>();
        domainKeywords.put("Finance", List.of("travel", "reimbursement", "expense", "expenses", "finance"));
        domainKeywords.put("HR", List.of("leave", "employee", "employees", "holiday", "vacation"));
        domainKeywords.put("Architecture", List.of("api", "architecture", "migration", "versioning", "release", "strategy"));
        domainKeywords.put("Security", List.of("password", "rotation", "rotate", "credential", "credentials", "security"));
        domainKeywords.put("Operations", List.of("incident", "operations", "severity", "production"));

        return domainKeywords.entrySet().stream()
                .max(Comparator.comparingInt(entry -> overlapCount(queryTokens, Set.copyOf(entry.getValue()))))
                .map(Map.Entry::getKey)
                .orElse("Knowledge");
    }

    private int overlapScore(Set<String> queryTokens, Set<String> keywords) {
        return overlapCount(queryTokens, keywords);
    }

    private int overlapCount(Set<String> queryTokens, Set<String> keywords) {
        return (int) queryTokens.stream().filter(keywords::contains).count();
    }

    private Set<String> tokenize(String value) {
        return List.of(value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .stream()
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}