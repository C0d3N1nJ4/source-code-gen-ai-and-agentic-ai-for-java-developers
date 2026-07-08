package com.acme.assistant.ingestion;

import com.acme.assistant.model.EnterpriseDocument;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EnterpriseDocumentCatalog {

    public List<EnterpriseDocument> defaultDocuments() {
        return List.of(
                new EnterpriseDocument(
                        "Remote Work Policy",
                        "remote-work-policy",
                        "Employees may work remotely up to three days per week with manager approval. "
                                + "Remote work during international travel requires manager approval and "
                                + "confirmation that data protection requirements are met.",
                        Map.of("policyType", "remote-work")),
                new EnterpriseDocument(
                        "Travel Policy",
                        "travel-policy",
                        "Employees travelling internationally must submit an itinerary before departure and "
                                + "must comply with country-specific legal and security guidance from Acme Security.",
                        Map.of("policyType", "travel")),
                new EnterpriseDocument(
                        "Expense Claim Policy",
                        "expense-claim-policy",
                        "International expense claims must include receipts in original currency and a converted "
                                + "amount in USD. Claims are submitted through the finance portal within 10 business days.",
                        Map.of("policyType", "expense")),
                new EnterpriseDocument(
                        "Employee Handbook",
                        "employee-handbook",
                        "Employees should consult HR for policy clarifications and use official channels for exceptions.",
                        Map.of("policyType", "handbook")),
                new EnterpriseDocument(
                        "IT Security Policy",
                        "it-security-policy",
                        "When working remotely, employees must use company-managed devices, VPN, and multi-factor authentication. "
                                + "Public Wi-Fi is only permitted with VPN enabled.",
                        Map.of("policyType", "security")));
    }

}
