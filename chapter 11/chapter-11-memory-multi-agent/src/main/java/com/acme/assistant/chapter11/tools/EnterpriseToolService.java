package com.acme.assistant.chapter11.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Local enterprise tools used through specialist agents.
 */
@Service
public class EnterpriseToolService {

    public Map<String, Object> submitLeave(String userId, String leaveDate, String reason) {
        return Map.of(
                "status", "SUBMITTED",
                "userId", userId,
                "leaveDate", leaveDate,
                "reason", reason,
                "requestId", "LV-" + Math.abs((userId + leaveDate).hashCode()));
    }

    public Map<String, Object> estimateTravelExpenses(String destination, int days) {
        BigDecimal base = switch (destination.toLowerCase()) {
            case "amsterdam", "london", "paris" -> BigDecimal.valueOf(420);
            case "new york", "san francisco" -> BigDecimal.valueOf(690);
            default -> BigDecimal.valueOf(300);
        };
        BigDecimal total = base.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
        return Map.of(
                "destination", destination,
                "days", days,
                "currency", "EUR",
                "estimatedTotal", total);
    }

    public Map<String, Object> createExpenseClaim(String userId, String category, BigDecimal amount) {
        return Map.of(
                "claimId", "EXP-" + Math.abs((userId + category + amount).hashCode()),
                "status", "CREATED",
                "userId", userId,
                "category", category,
                "amount", amount);
    }

    public Map<String, Object> lookupEmployee(String employeeId) {
        return Map.of(
                "employeeId", employeeId,
                "name", "Alex Morgan",
                "department", "Architecture",
                "manager", "Priya Shah",
                "location", "Amsterdam");
    }

    public Map<String, Object> restartServer(String environment, String serviceName) {
        return Map.of(
                "environment", environment,
                "serviceName", serviceName,
                "status", "RESTART_REQUESTED",
                "ticketId", "OPS-" + Math.abs((environment + serviceName).hashCode()));
    }
}
