package com.acme.assistant.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LeaveAgent {

    private static final Pattern INTENT_PATTERN = Pattern.compile("\\\"intent\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern TARGET_DATE_PATTERN = Pattern.compile("\\\"targetDate\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern REASON_PATTERN = Pattern.compile("\\\"reason\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private final ChatClient chatClient;
    private final Clock clock;

    public LeaveAgent(ChatClient chatClient, Clock clock) {
        this.chatClient = chatClient;
        this.clock = clock;
    }

    public AgentDecision decide(String employeeMessage, String policyContext) {
        String prompt = """
                You are an enterprise planning assistant.
                Determine user intent for annual leave requests.
                Respond with JSON only in this format:
                {\"intent\":\"SUBMIT_LEAVE_REQUEST|ANSWER_ONLY\",\"targetDate\":\"YYYY-MM-DD|UNKNOWN\",\"reason\":\"short reason\"}

                Policy Context:
                %s

                User Message:
                %s
                """.formatted(policyContext, employeeMessage);

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            AgentDecision decision = parseDecision(response);
            return normalizeDecision(decision, employeeMessage);
        } catch (Exception ignored) {
            return fallbackDecision(employeeMessage);
        }
    }

    private AgentDecision parseDecision(String response) {
        String intent = extractValue(INTENT_PATTERN, response, "ANSWER_ONLY");
        String targetDate = extractValue(TARGET_DATE_PATTERN, response, "UNKNOWN");
        String reason = extractValue(REASON_PATTERN, response, "Derived from model response");
        return new AgentDecision(intent, targetDate, reason);
    }

    private AgentDecision normalizeDecision(AgentDecision decision, String employeeMessage) {
        String normalizedIntent = decision.intent() == null ? "ANSWER_ONLY" : decision.intent().trim();
        String normalizedDate = normalizeDate(decision.targetDate(), employeeMessage);
        String reason = decision.reason() == null || decision.reason().isBlank()
                ? "Derived from user request"
                : decision.reason();
        return new AgentDecision(normalizedIntent, normalizedDate, reason);
    }

    private AgentDecision fallbackDecision(String employeeMessage) {
        String message = employeeMessage.toLowerCase();
        if (message.contains("annual leave") || message.contains("book leave") || message.contains("next friday")) {
            return new AgentDecision("SUBMIT_LEAVE_REQUEST", nextFriday().toString(), "Fallback parser detected leave request intent");
        }
        return new AgentDecision("ANSWER_ONLY", "UNKNOWN", "Fallback parser could not detect leave-booking intent");
    }

    private String normalizeDate(String targetDate, String employeeMessage) {
        if (targetDate == null || targetDate.isBlank() || "UNKNOWN".equalsIgnoreCase(targetDate)) {
            if (employeeMessage.toLowerCase().contains("next friday")) {
                return nextFriday().toString();
            }
            return "UNKNOWN";
        }

        try {
            LocalDate.parse(targetDate);
            return targetDate;
        } catch (DateTimeParseException ex) {
            return "UNKNOWN";
        }
    }

    private String extractValue(Pattern pattern, String source, String defaultValue) {
        Matcher matcher = pattern.matcher(source == null ? "" : source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defaultValue;
    }

    private LocalDate nextFriday() {
        return LocalDate.now(clock).with(TemporalAdjusters.next(java.time.DayOfWeek.FRIDAY));
    }
}
