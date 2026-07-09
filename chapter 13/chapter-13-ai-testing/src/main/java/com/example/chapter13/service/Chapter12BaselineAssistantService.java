package com.example.chapter13.service;

import com.example.chapter13.model.EnterpriseKnowledgeDocument;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class Chapter12BaselineAssistantService {

    public List<EnterpriseKnowledgeDocument> authorizedKnowledgeDocuments() {
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

    public List<String> securityRoles() {
        return List.of("EMPLOYEE", "HR", "FINANCE", "OPERATIONS", "ADMIN");
    }

    public List<String> specialistAgents() {
        return List.of("HR", "FINANCE", "OPERATIONS", "KNOWLEDGE");
    }

    public String selectAgent(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("leave") || normalized.contains("employee")) {
            return "HR";
        }
        if (normalized.contains("expense") || normalized.contains("invoice") || normalized.contains("budget")) {
            return "FINANCE";
        }
        if (normalized.contains("server") || normalized.contains("deployment") || normalized.contains("restart") || normalized.contains("incident")) {
            return "OPERATIONS";
        }
        return "KNOWLEDGE";
    }

    public Map<String, Set<String>> allowedMcpCapabilities() {
        return Map.of(
                "HR", Set.of("hr.lookupEmployee", "hr.submitLeave", "hr.approveLeave"),
                "FINANCE", Set.of("finance.submitExpense", "finance.approveExpense"),
                "OPERATIONS", Set.of("ops.restartServer"),
                "KNOWLEDGE", Set.of("knowledge.search"));
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

    public List<String> governanceSensitiveOperations() {
        return List.of("restart production server", "high-value expense approval");
    }
}