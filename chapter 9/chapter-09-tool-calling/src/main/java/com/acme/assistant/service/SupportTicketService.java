package com.acme.assistant.service;

import com.acme.assistant.entity.SupportTicketEntity;
import com.acme.assistant.model.SupportTicket;
import com.acme.assistant.repository.SupportTicketRepository;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketService {

    private final CustomerService customerService;
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(CustomerService customerService, SupportTicketRepository supportTicketRepository) {
        this.customerService = customerService;
        this.supportTicketRepository = supportTicketRepository;
    }

    public SupportTicket createSupportTicket(SupportTicket request) {
        customerService.findCustomer(request.customerId());

        String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        SupportTicketEntity entity = new SupportTicketEntity(
            ticketId,
            request.customerId(),
            request.summary(),
            request.priority(),
            "OPEN");

        supportTicketRepository.save(entity);

        return new SupportTicket(
                entity.getId(),
                entity.getCustomerId(),
                entity.getSummary(),
                entity.getPriority(),
                entity.getStatus());
    }
}
