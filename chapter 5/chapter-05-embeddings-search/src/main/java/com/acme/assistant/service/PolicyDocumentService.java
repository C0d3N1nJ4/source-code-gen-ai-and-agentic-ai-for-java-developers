package com.acme.assistant.service;

import com.acme.assistant.model.PolicyDocument;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PolicyDocumentService {

    private final Map<String, PolicyDocument> catalog;

    public PolicyDocumentService() {
        this.catalog = buildCatalog();
    }

    public List<PolicyDocument> loadDocuments(List<String> requestedIds) {
        if (requestedIds == null || requestedIds.isEmpty()) {
            return List.copyOf(catalog.values());
        }

        Set<String> normalizedIds = requestedIds.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<PolicyDocument> found = catalog.entrySet().stream()
                .filter(entry -> normalizedIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        if (found.isEmpty()) {
            throw new IllegalArgumentException("No matching policy documents found for the provided documentIds");
        }
        return found;
    }

    public List<String> availableDocumentIds() {
        return List.copyOf(catalog.keySet());
    }

    private Map<String, PolicyDocument> buildCatalog() {
        Map<String, PolicyDocument> documents = new LinkedHashMap<>();

        documents.put("remote-work", new PolicyDocument(
                "remote-work",
                "Remote Work Policy",
                "Employees may work remotely up to three days per week.\n\n"
                        + "Remote work arrangements must be approved by the employee's manager and recorded in the HR system.\n\n"
                        + "Employees working remotely must remain available during core collaboration hours from 10:00 to 16:00 local time.",
                Map.of("department", "HR", "policyType", "remote-work", "version", "2026.1")));

        documents.put("annual-leave", new PolicyDocument(
                "annual-leave",
                "Annual Leave Policy",
                "Full-time employees receive 20 days of annual leave per calendar year.\n\n"
                        + "Leave requests should be submitted at least two weeks in advance and require manager approval.\n\n"
                        + "Unused leave can be carried forward up to five days into the next calendar year.",
                Map.of("department", "HR", "policyType", "annual-leave", "version", "2026.1")));

        documents.put("expense-claims", new PolicyDocument(
                "expense-claims",
                "Expense Claim Policy",
                "Employees must submit expense claims within 30 days of purchase.\n\n"
                        + "Valid claims include approved travel, accommodation, and client meeting costs with itemized receipts.\n\n"
                        + "Expense approvals follow manager review and finance verification before reimbursement.",
                Map.of("department", "Finance", "policyType", "expense-claims", "version", "2026.1")));

        documents.put("password-policy", new PolicyDocument(
                "password-policy",
                "Password Policy",
                "Corporate passwords must be at least 12 characters and include upper-case, lower-case, numeric, and special characters.\n\n"
                        + "Passwords must be rotated every 90 days and must not be reused for at least 12 cycles.\n\n"
                        + "Multi-factor authentication is mandatory for all systems containing customer or financial data.",
                Map.of("department", "Security", "policyType", "password-policy", "version", "2026.1")));

        return documents;
    }
}
