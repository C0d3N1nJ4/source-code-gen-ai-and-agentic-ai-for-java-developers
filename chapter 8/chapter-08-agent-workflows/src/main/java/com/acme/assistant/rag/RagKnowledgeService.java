package com.acme.assistant.rag;

import org.springframework.stereotype.Service;

@Service
public class RagKnowledgeService {

    public String getLeavePolicyContext() {
        return "Acme Leave Policy: Employees can request annual leave in full-day increments. "
                + "Leave requests should be submitted before the leave date and require available balance. "
                + "Manager approval is required after submission by the HR workflow.";
    }
}
