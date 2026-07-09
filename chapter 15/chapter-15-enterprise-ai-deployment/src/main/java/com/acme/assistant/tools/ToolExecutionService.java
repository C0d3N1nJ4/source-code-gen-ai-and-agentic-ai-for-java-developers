package com.acme.assistant.tools;

import com.acme.assistant.config.AssistantProperties;
import com.acme.assistant.observability.AiMetricsService;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ToolExecutionService {

    private final AssistantProperties properties;
    private final TaskExecutor taskExecutor;
    private final AiMetricsService metricsService;

    public ToolExecutionService(AssistantProperties properties, TaskExecutor taskExecutor, AiMetricsService metricsService) {
        this.properties = properties;
        this.taskExecutor = taskExecutor;
        this.metricsService = metricsService;
    }

    public String execute(String toolName, Map<String, Object> args) {
        if (!properties.getTools().getAllowList().contains(toolName)) {
            throw new IllegalArgumentException("Tool not allowed: " + toolName);
        }

        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> runTool(toolName, args), command -> taskExecutor.execute(command));
        try {
            String result = task.get(3, TimeUnit.SECONDS);
            metricsService.recordToolCall();
            return result;
        } catch (Exception ex) {
            return "Tool execution failed: " + ex.getMessage();
        }
    }

    private String runTool(String toolName, Map<String, Object> args) {
        return switch (toolName) {
            case "weather" -> "Weather in " + args.getOrDefault("city", "unknown") + " is 21C and clear";
            case "employee-directory" -> "Employee record: " + args.getOrDefault("employeeId", "N/A") + " -> Jane Doe, Engineering";
            case "company-policies" -> "Policy " + args.getOrDefault("policy", "general") + " approved and published";
            case "calendar" -> "Calendar event scheduled on " + args.getOrDefault("date", LocalDate.now());
            case "calculator" -> calculate(args);
            default -> "Unsupported tool";
        };
    }

    private String calculate(Map<String, Object> args) {
        double left = Double.parseDouble(String.valueOf(args.getOrDefault("left", 0)));
        double right = Double.parseDouble(String.valueOf(args.getOrDefault("right", 0)));
        String op = String.valueOf(args.getOrDefault("operator", "+"));
        return switch (op) {
            case "-" -> String.valueOf(left - right);
            case "*" -> String.valueOf(left * right);
            case "/" -> right == 0 ? "Division by zero" : String.valueOf(left / right);
            default -> String.valueOf(left + right);
        };
    }
}
