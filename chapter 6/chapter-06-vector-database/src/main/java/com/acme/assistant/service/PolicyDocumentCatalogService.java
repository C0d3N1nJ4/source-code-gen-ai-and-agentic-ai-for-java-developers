package com.acme.assistant.service;

import com.acme.assistant.model.PolicyDocumentPayload;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PolicyDocumentCatalogService {

    public List<PolicyDocumentPayload> defaultPolicies() {
        return List.of(
                new PolicyDocumentPayload(
                        "remote-work-policy",
                        "Remote Work Policy",
                        "Employees may work remotely up to three days per week.\n\n"
                                + "Remote work requests require manager approval and must be logged in the HR portal.\n\n"
                                + "Employees working remotely must remain available between 10:00 and 16:00 local time.",
                        "HR",
                        "Remote Work",
                        "acme://policies/hr/remote-work"),
                new PolicyDocumentPayload(
                        "annual-leave-policy",
                        "Annual Leave Policy",
                        "Full-time employees receive 20 days of annual leave per year.\n\n"
                                + "Leave should be requested at least 10 business days in advance for manager planning.\n\n"
                                + "Carry-over is capped at five days into the next calendar year.",
                        "HR",
                        "Leave",
                        "acme://policies/hr/annual-leave"),
                new PolicyDocumentPayload(
                        "expense-claim-policy",
                        "Expense Claim Policy",
                        "Expense claims must be submitted within 30 days and include itemized receipts.\n\n"
                                + "Eligible claims include approved travel, accommodation, and client meeting costs.\n\n"
                                + "Finance reviews and reimburses approved claims in the next payroll cycle.",
                        "Finance",
                        "Expenses",
                        "acme://policies/finance/expenses"),
                new PolicyDocumentPayload(
                        "password-policy",
                        "Password Policy",
                        "Passwords must contain at least 12 characters and include upper-case, lower-case, numeric, and special characters.\n\n"
                                + "Passwords expire every 90 days and must not be reused for 12 cycles.\n\n"
                                + "Multi-factor authentication is mandatory for systems with customer data.",
                        "Security",
                        "Identity and Access",
                        "acme://policies/security/passwords"),
                new PolicyDocumentPayload(
                        "travel-policy",
                        "Travel Policy",
                        "International travel requires manager approval and travel risk review before booking.\n\n"
                                + "Employees may work remotely while travelling abroad only if local legal and security controls are satisfied.\n\n"
                                + "Corporate VPN and device encryption are mandatory during travel.",
                        "Operations",
                        "Travel",
                        "acme://policies/operations/travel"));
    }
}
