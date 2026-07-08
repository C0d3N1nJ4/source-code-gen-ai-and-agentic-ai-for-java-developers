package com.acme.assistant.service;

import com.acme.assistant.entity.CustomerEntity;
import com.acme.assistant.exception.NotFoundException;
import com.acme.assistant.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerService customerService = new CustomerService(customerRepository);

    @Test
    void shouldFindCustomer() {
        when(customerRepository.findById("ACME-1024"))
                .thenReturn(Optional.of(new CustomerEntity("ACME-1024", "Acme Retail North", "GOLD", "ACTIVE")));

        var customer = customerService.findCustomer("ACME-1024");

        assertThat(customer.customerId()).isEqualTo("ACME-1024");
        assertThat(customer.name()).isEqualTo("Acme Retail North");
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        when(customerRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findCustomer("UNKNOWN"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found");
    }
}
