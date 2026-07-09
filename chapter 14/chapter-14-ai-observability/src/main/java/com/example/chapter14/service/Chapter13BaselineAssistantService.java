package com.example.chapter14.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class Chapter13BaselineAssistantService {

    public List<EnterpriseKnowledgeDocument> knowledgeDocuments() {
        return List.of(
                new EnterpriseKnowledgeDocument(
                        "mcp://hr/policies",
                        "HR",
                        "HR Policy Handbook",
                        "Remote work is allowed up to three days weekly. Travel requires manager approval and new employees receive 25 days of annual leave.",
                        Set.of("hr", "leave", "travel", "policy", "manager", "approval", "days")),
                new EnterpriseKnowledgeDocument(
                        "mcp://engineering/github/releases",
                        "Architecture",
                        "GitHub Releases",
                        "Release 2026.06 includes payment retry fixes, improved API latency, and gateway-led rollout guidance for versioned REST APIs.",
                        Set.of("engineering", "release", "api", "latency", "versioned", "rest", "rollout")),
                new EnterpriseKnowledgeDocument(
                        "mcp://support/tickets/open",
                        "Operations",
                        "Open Support Tickets",
                        "Open incidents are tracked with priority, status, and customer impact for production support response.",
                        Set.of("support", "tickets", "incident", "priority", "status", "production", "customer")),
                new EnterpriseKnowledgeDocument(
                        "finance-travel-policy",
                        "Finance",
                        "Travel Reimbursement Policy",
                        "Employees may claim transport, accommodation, and meals within approved reimbursement limits for Project Orion and related enterprise travel.",
                        Set.of("travel", "reimbursement", "expenses", "finance", "policy", "meals", "project", "orion")),
                new EnterpriseKnowledgeDocument(
                        "security-password-policy",
                        "Security",
                        "Password Rotation Policy",
                        "Privileged accounts rotate credentials every 90 days and must use managed secret storage under enterprise security controls.",
                        Set.of("password", "rotation", "security", "credentials", "policy", "secret", "managed")));
    }

    public List<String> mcpTools() {
        return List.of("create_jira_ticket", "send_incident_email", "update_customer_record");
    }

    public List<String> specialistAgents() {
        return List.of("HR Agent", "Finance Agent", "Knowledge Agent", "Operations Agent", "Coordinator Agent");
    }

    public Map<String, String> userProfile() {
        return Map.of(
                "preferredLanguage", "Java",
                "preferredCurrency", "Euro",
                "preferredOffice", "Amsterdam");
    }

    public List<String> semanticMemories() {
        return List.of(
                "Project Orion workshop is in Amsterdam next week.",
                "Finance estimates should default to euro for this employee.",
                "Previous conversation requested a manager-ready travel summary.");
    }

    public PromptComparison defaultPromptComparison() {
        List<String> scenarios = List.of(
                "How many leave days do I receive?",
                "Explain the travel reimbursement policy.",
                "Summarize the API migration strategy.",
                "What is our password rotation policy?");
        String baselinePrompt = "You are an enterprise assistant.";
        String candidatePrompt = "You are an enterprise assistant. Answer only using approved enterprise documentation. Respond concisely. Never speculate.";

        VersionScore baseline = scoreVersion("baseline", baselinePrompt, scenarios);
        VersionScore candidate = scoreVersion("candidate", candidatePrompt, scenarios);

        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (String scenario : scenarios) {
            int baselineScenarioScore = scoreScenario(baselinePrompt, scenario);
            int candidateScenarioScore = scoreScenario(candidatePrompt, scenario);
            String winner = candidateScenarioScore >= baselineScenarioScore ? "candidate" : "baseline";
            String reason = candidateScenarioScore >= baselineScenarioScore
                    ? "Candidate prompt stays more grounded and concise for this business case."
                    : "Baseline prompt remains stronger for this scenario.";
            scenarioResults.add(new ScenarioResult(scenario, winner, reason));
        }

        Map<String, Integer> qualityDelta = new LinkedHashMap<>();
        qualityDelta.put("accuracy", candidate.accuracy() - baseline.accuracy());
        qualityDelta.put("groundedness", candidate.groundedness() - baseline.groundedness());
        qualityDelta.put("safety", candidate.safety() - baseline.safety());
        qualityDelta.put("conciseness", candidate.conciseness() - baseline.conciseness());
        qualityDelta.put("aggregateScore", candidate.aggregateScore() - baseline.aggregateScore());

        String recommendedVersion = candidate.aggregateScore() >= baseline.aggregateScore() ? "candidate" : "baseline";
        return new PromptComparison(recommendedVersion, baseline, candidate, scenarioResults, qualityDelta);
    }

    public RagEvaluation defaultRagEvaluation() {
        return evaluateRetrieval("What is Acme Corporation travel reimbursement policy?", 3);
    }

    public RagEvaluation evaluateRetrieval(String question, int maxDocuments) {
        long start = System.nanoTime();
        Set<String> queryTokens = tokenize(question);
        String expectedDomain = inferExpectedDomain(queryTokens);
        List<EnterpriseKnowledgeDocument> documents = knowledgeDocuments();

        List<RetrievedDocument> scoredDocuments = documents.stream()
                .map(document -> new RetrievedDocument(
                        document.documentId(),
                        document.domain(),
                        document.title(),
                        document.excerpt(),
                        overlapCount(queryTokens, document.keywords())))
                .sorted(Comparator.comparingInt(RetrievedDocument::score).reversed())
                .toList();

        int bestScore = scoredDocuments.isEmpty() ? 0 : scoredDocuments.getFirst().score();
        int relevanceThreshold = Math.max(1, bestScore);

        List<RetrievedDocument> retrievedDocuments = scoredDocuments.stream()
                .filter(document -> document.score() >= relevanceThreshold)
                .limit(maxDocuments)
                .toList();
        if (retrievedDocuments.isEmpty()) {
            retrievedDocuments = scoredDocuments.stream().limit(maxDocuments).toList();
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

        return new RagEvaluation(
                question,
                expectedDomain,
                retrievedDocuments,
                new RetrievalMetrics(relevance, precision, recall, rankingQuality, groundedness, latencyMs),
                generatedAnswer,
                List.of(
                        "Retrieval is evaluated independently from final response generation.",
                        "The evaluation corpus reuses chapter 12 MCP resources and chapter 11 business travel context.",
                        "Use this report after document, chunking, or embedding changes to catch regressions early."));
    }

    public ToolCallingSuite toolCallingSuite() {
        List<ToolCase> cases = List.of(
                evaluateToolScenario("Submit annual leave for next Friday"),
                evaluateToolScenario("Create expense report for Amsterdam conference"),
                evaluateToolScenario("Lookup employee EMP-1001 details"),
                evaluateToolScenario("Restart server PROD-DB-01 without admin role"));

        int unauthorizedCalls = (int) cases.stream()
                .filter(c -> !c.authorized() && !"denied".equals(c.outcome()))
                .count();
        int authorizationEnforcement = unauthorizedCalls == 0 ? 100 : 80;

        return new ToolCallingSuite(
                "tool-calling-regression",
                new ToolCallingMetrics(100, 100, authorizationEnforcement, 100, 100, unauthorizedCalls, 24),
                cases,
                List.of(
                        "Tool selection validates leave, expense, employee lookup, and server operations use cases.",
                        "Authorization checks block privileged restart operations for unauthorized callers.",
                        "Failure scenarios return deterministic business outcomes suitable for CI regression."));
    }

    public McpSuite mcpSuite() {
        return new McpSuite(
                "mcp-regression",
                new McpMetrics(100, 100, 100, 100, 100, 100, 100, 100, 31),
                List.of(
                        new McpCase("discovery", true, true, "found-3-servers", true),
                        new McpCase("capability-lookup", true, true, "resolved-create_jira_ticket", true),
                        new McpCase("serialization", true, true, "payload-roundtrip-ok", true),
                        new McpCase("timeout-retry", true, true, "retried-once-success", true),
                        new McpCase("authorization", true, false, "graceful-failure", true)),
                List.of(
                        "MCP discovery and capability lookup are validated before invocation.",
                        "Serialization and deserialization checks prevent schema drift.",
                        "Timeout and authorization paths validate retries and graceful failure handling."));
    }

    public AgentSuite agentSuite() {
        return new AgentSuite(
                "agent-orchestration-regression",
                new AgentMetrics(99, 99, 99, 98, 100, 100, 126),
                List.of(
                        new AgentCase(
                                "Travel request requiring HR, Finance, and Notification coordination",
                                List.of("Coordinator Agent", "HR Agent", "Finance Agent", "Notification Agent"),
                                "completed",
                                true),
                        new AgentCase(
                                "Architecture question requiring only Knowledge Agent",
                                List.of("Coordinator Agent", "Knowledge Agent"),
                                "completed",
                                true),
                        new AgentCase(
                                "Finance partial outage while preparing approval response",
                                List.of("Coordinator Agent", "Finance Agent", "Notification Agent"),
                                "partial-failure-handled",
                                true)),
                List.of(
                        "Coordinator plans and routes work to specialist agents with deterministic assertions.",
                        "Delegation and aggregation are tested independently from LLM wording variability.",
                        "Partial failures are isolated and still produce safe aggregated outputs."));
    }

    public WorkflowSuite workflowSuite() {
        List<WorkflowCase> cases = List.of(
                evaluateWorkflowScenario("I'd like to book annual leave for next Friday."),
                evaluateWorkflowScenario("Estimate my travel expenses for Amsterdam next week for Project Orion."),
                evaluateWorkflowScenario("Summarize the latest engineering release notes and API migration strategy from enterprise knowledge."),
                evaluateWorkflowScenario("Create an incident ticket and notify leadership because the finance system is unavailable."));

        int passedCases = (int) cases.stream().filter(WorkflowCase::passed).count();
        int gracefulFailureRate = cases.stream().anyMatch(result -> "graceful-failure".equals(result.outcome())) ? 100 : 0;

        return new WorkflowSuite(
                "enterprise-workflow-regression",
                new WorkflowMetrics(100, 100, 100, gracefulFailureRate, (passedCases * 100) / cases.size()),
                cases,
                List.of(
                        "Tool selection is validated separately from business validation logic.",
                        "Workflow scenarios reuse chapter 12 MCP tool names: " + mcpTools() + ".",
                        "These deterministic workflow suites simulate chapter 11 coordinator-to-specialist behavior without calling real enterprise systems."));
    }

    public ConversationEvaluation defaultConversationEvaluation() {
        List<String> transcript = new ArrayList<>();
        transcript.add("user: I'm travelling to Amsterdam next week for the Project Orion workshop.");
        transcript.add("assistant: Noted. I will keep Amsterdam and Project Orion in context using conversation and semantic memory.");
        transcript.add("user: Estimate my expenses.");
        transcript.add("assistant: Estimated travel expenses for Amsterdam are shown in euro using your stored profile " + userProfile() + ".");
        transcript.add("user: Update my preferred currency to euro.");
        transcript.add("assistant: Preference updated. Future travel estimates will use euro.");
        transcript.add("user: Continue the workflow and summarize it for my manager.");
        transcript.add("assistant: Continued the Project Orion travel workflow, reused semantic memories " + semanticMemories() + ", and prepared a manager summary.");

        return new ConversationEvaluation(
                "conv-1301",
                "EMP-1001",
                new ConversationMetrics(98, 96, 100, 100, 99),
                transcript,
                List.of(
                        "Conversation retains Amsterdam and Project Orion context across turns.",
                        "Preference updates immediately affect later workflow steps.",
                        "User memory remains isolated from other employees while sharing only coordinator-safe context with specialist agents " + specialistAgents() + "."));
    }

    private VersionScore scoreVersion(String version, String prompt, List<String> scenarios) {
        String normalized = prompt.toLowerCase(Locale.ROOT);
        boolean enterprise = normalized.contains("enterprise");
        boolean documentation = normalized.contains("documentation") || normalized.contains("company policies");
        boolean concise = normalized.contains("concise");
        boolean noSpeculation = normalized.contains("never speculate");
        boolean references = normalized.contains("reference") || normalized.contains("approved");
        boolean confidentiality = normalized.contains("confidential") || normalized.contains("never reveal");

        int scenarioCoverage = scenarios.size() >= 4 ? 4 : scenarios.size();
        int accuracy = clamp(80 + bonus(enterprise, 2) + bonus(documentation, 9) + bonus(noSpeculation, 6) + scenarioCoverage);
        int relevance = clamp(84 + bonus(enterprise, 5) + scenarioCoverage);
        int completeness = clamp(79 + bonus(documentation, 6) + bonus(references, 5) + scenarioCoverage);
        int conciseness = clamp(70 + bonus(concise, 20) + bonus(noSpeculation, 4));
        int groundedness = clamp(76 + bonus(documentation, 14) + bonus(noSpeculation, 8));
        int safety = clamp(80 + bonus(noSpeculation, 9) + bonus(confidentiality, 6) + bonus(documentation, 4));
        int tone = clamp(84 + bonus(enterprise, 4) + bonus(concise, 3));
        int averageResponseLength = Math.max(170, 340 - bonus(concise, 90) - bonus(documentation, 30));
        int aggregate = (accuracy + relevance + completeness + conciseness + groundedness + safety + tone) / 7;

        List<String> strengths = new ArrayList<>();
        if (documentation) {
            strengths.add("Grounds responses in approved documentation");
        }
        if (concise) {
            strengths.add("Reduces verbose responses");
        }
        if (noSpeculation) {
            strengths.add("Suppresses unsupported claims");
        }
        if (strengths.isEmpty()) {
            strengths.add("Provides general enterprise assistant framing");
        }

        return new VersionScore(version, accuracy, relevance, completeness, conciseness, groundedness, safety, tone, averageResponseLength, aggregate, strengths);
    }

    private int scoreScenario(String prompt, String scenario) {
        String normalizedPrompt = prompt.toLowerCase(Locale.ROOT);
        String normalizedScenario = scenario.toLowerCase(Locale.ROOT);
        int score = 78;
        if (normalizedPrompt.contains("documentation") || normalizedPrompt.contains("approved")) {
            score += 10;
        }
        if (normalizedPrompt.contains("concise")) {
            score += 6;
        }
        if (normalizedPrompt.contains("never speculate")) {
            score += 8;
        }
        if (normalizedScenario.contains("policy") && normalizedPrompt.contains("documentation")) {
            score += 4;
        }
        if (normalizedScenario.contains("api") && normalizedPrompt.contains("enterprise")) {
            score += 2;
        }
        return clamp(score);
    }

    private ToolCase evaluateToolScenario(String scenario) {
        String normalized = scenario.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave")) {
            return new ToolCase(scenario, "submitLeave", Map.of("date", "next Friday", "leaveType", "ANNUAL"), true, "approved", true);
        }
        if (normalized.contains("expense")) {
            return new ToolCase(scenario, "createExpense", Map.of("destination", "Amsterdam", "category", "Travel"), true, "created", true);
        }
        if (normalized.contains("lookup")) {
            return new ToolCase(scenario, "lookupEmployee", Map.of("employeeId", "EMP-1001"), true, "returned", true);
        }
        return new ToolCase(scenario, "restartServer", Map.of("serverId", "PROD-DB-01"), false, "denied", true);
    }

    private WorkflowCase evaluateWorkflowScenario(String scenario) {
        String normalized = scenario.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave")) {
            return new WorkflowCase(scenario, "SubmitLeaveRequest", Map.of("date", "next Friday", "leaveType", "Annual Leave"), "approved", true);
        }
        if (normalized.contains("incident") || normalized.contains("notify")) {
            return new WorkflowCase(scenario, "create_jira_ticket + send_incident_email", Map.of("title", "Finance system unavailable", "priority", "HIGH"), "graceful-failure", true);
        }
        if (normalized.contains("expenses")) {
            return new WorkflowCase(scenario, "EstimateExpenses", Map.of("destination", "Amsterdam", "timeframe", "next week", "project", "Project Orion"), "completed", true);
        }
        return new WorkflowCase(scenario, "Knowledge Agent", Map.of("resource", "mcp://engineering/github/releases", "topic", "API migration strategy"), "completed", true);
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

    private Set<String> tokenize(String value) {
        return List.of(value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .stream()
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());
    }

    private int overlapCount(Set<String> queryTokens, Set<String> keywords) {
        return (int) queryTokens.stream().filter(keywords::contains).count();
    }

    private int bonus(boolean condition, int amount) {
        return condition ? amount : 0;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    public record EnterpriseKnowledgeDocument(
            String documentId,
            String domain,
            String title,
            String excerpt,
            Set<String> keywords) {
    }

    public record PromptComparison(
            String recommendedVersion,
            VersionScore baseline,
            VersionScore candidate,
            List<ScenarioResult> scenarios,
            Map<String, Integer> qualityDelta) {
    }

    public record VersionScore(
            String version,
            int accuracy,
            int relevance,
            int completeness,
            int conciseness,
            int groundedness,
            int safety,
            int tone,
            int averageResponseLength,
            int aggregateScore,
            List<String> strengths) {
    }

    public record ScenarioResult(String scenario, String winningVersion, String reason) {
    }

    public record RagEvaluation(
            String question,
            String expectedDomain,
            List<RetrievedDocument> retrievedDocuments,
            RetrievalMetrics metrics,
            String generatedAnswer,
            List<String> findings) {
    }

    public record RetrievedDocument(String documentId, String domain, String title, String excerpt, int score) {
    }

    public record RetrievalMetrics(int relevance, int precision, int recall, int rankingQuality, int groundedness, long retrievalLatencyMs) {
    }

    public record ToolCallingSuite(String suiteName, ToolCallingMetrics metrics, List<ToolCase> cases, List<String> observations) {
    }

    public record ToolCallingMetrics(
            int toolSelectionAccuracy,
            int parameterExtractionAccuracy,
            int authorizationEnforcement,
            int businessRuleExecution,
            int failureHandlingRate,
            int unauthorizedToolCalls,
            long averageToolLatencyMs) {
    }

    public record ToolCase(String scenario, String selectedTool, Map<String, String> parameters, boolean authorized, String outcome, boolean passed) {
    }

    public record McpSuite(String suiteName, McpMetrics metrics, List<McpCase> cases, List<String> observations) {
    }

    public record McpMetrics(
            int discoverySuccessRate,
            int authenticationRate,
            int authorizationRate,
            int capabilityLookupRate,
            int serializationRate,
            int retryHandlingRate,
            int timeoutHandlingRate,
            int gracefulFailureRate,
            long averageMcpLatencyMs) {
    }

    public record McpCase(String capability, boolean authenticated, boolean authorized, String outcome, boolean passed) {
    }

    public record AgentSuite(String suiteName, AgentMetrics metrics, List<AgentCase> cases, List<String> observations) {
    }

    public record AgentMetrics(
            int planningAccuracy,
            int routingAccuracy,
            int delegationAccuracy,
            int aggregationQuality,
            int partialFailureHandling,
            int agentIsolation,
            long averageWorkflowDurationMs) {
    }

    public record AgentCase(String scenario, List<String> routedAgents, String outcome, boolean passed) {
    }

    public record WorkflowSuite(String suiteName, WorkflowMetrics metrics, List<WorkflowCase> cases, List<String> observations) {
    }

    public record WorkflowMetrics(
            int toolSelectionAccuracy,
            int parameterAccuracy,
            int businessValidationRate,
            int gracefulFailureRate,
            int workflowCompletionRate) {
    }

    public record WorkflowCase(String scenario, String selectedTool, Map<String, String> parameters, String outcome, boolean passed) {
    }

    public record ConversationEvaluation(
            String conversationId,
            String userId,
            ConversationMetrics metrics,
            List<String> transcript,
            List<String> observations) {
    }

    public record ConversationMetrics(
            int contextRetention,
            int contextBoundaryRespect,
            int preferenceReuse,
            int userIsolation,
            int workflowCompletion) {
    }
}