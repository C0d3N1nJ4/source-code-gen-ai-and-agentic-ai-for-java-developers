package com.acme.assistant.tool;

import com.acme.assistant.model.SupportTicket;
import com.acme.assistant.service.SupportTicketService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketTools {

    private final SupportTicketService supportTicketService;

    public SupportTicketTools(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @Tool(description = "Create a support ticket for a customer issue with summary and priority.")
    public SupportTicket createSupportTicket(SupportTicket request) {
        return supportTicketService.createSupportTicket(request);
    }
}
