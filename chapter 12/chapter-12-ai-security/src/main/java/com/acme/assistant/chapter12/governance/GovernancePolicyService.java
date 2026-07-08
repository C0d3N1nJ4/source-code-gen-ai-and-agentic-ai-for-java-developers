package com.acme.assistant.chapter12.governance;

import com.acme.assistant.chapter12.config.AiSecurityProperties;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class GovernancePolicyService {

    private static final Pattern EXPENSE_PATTERN = Pattern.compile("expense\\s*[:=]?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private final AiSecurityProperties properties;

    public GovernancePolicyService(AiSecurityProperties properties) {
        this.properties = properties;
    }

    public GovernanceDecision evaluate(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);

        if (normalized.contains("restart production server") || normalized.contains("restart prod server")) {
            return new GovernanceDecision(true, "restart-production-server", "ADMIN", "Production restart requires administrator approval");
        }

        Matcher matcher = EXPENSE_PATTERN.matcher(message);
        if (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            if (amount > properties.getGovernance().getAutoApproveExpenseLimit()) {
                return new GovernanceDecision(true, "approve-expense", "FINANCE", "Expense above threshold requires human approval");
            }
        }

        return new GovernanceDecision(false, null, null, null);
    }

    public record GovernanceDecision(boolean requiresApproval, String operation, String requiredRole, String reason) {
    }
}
