package com.acme.assistant.service;

import com.acme.assistant.exception.NotFoundException;
import com.acme.assistant.model.Customer;
import com.acme.assistant.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomer(String customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));

        return new Customer(
                customer.getId(),
                customer.getName(),
                customer.getTier(),
                customer.getStatus());
    }
}
