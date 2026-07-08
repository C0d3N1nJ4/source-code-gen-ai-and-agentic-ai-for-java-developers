package com.acme.assistant.service;

import com.acme.assistant.model.Customer;
import com.acme.assistant.model.SupportTicket;
import com.acme.assistant.repository.SupportTicketRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportTicketServiceTest {

    private final CustomerService customerService = mock(CustomerService.class);
    private final SupportTicketRepository supportTicketRepository = mock(SupportTicketRepository.class);
    private final SupportTicketService supportTicketService = new SupportTicketService(customerService, supportTicketRepository);

    @Test
    void shouldCreateSupportTicket() {
        when(customerService.findCustomer("ACME-1024"))
                .thenReturn(new Customer("ACME-1024", "Acme Retail North", "GOLD", "ACTIVE"));

        SupportTicket request = new SupportTicket(
                null,
                "ACME-1024",
                "POS terminal keeps disconnecting",
                "HIGH",
                null);

        var result = supportTicketService.createSupportTicket(request);

        assertThat(result.ticketId()).startsWith("TCK-");
        assertThat(result.status()).isEqualTo("OPEN");
        verify(supportTicketRepository).save(any());
    }
}
