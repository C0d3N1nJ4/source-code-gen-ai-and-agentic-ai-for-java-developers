package com.acme.assistant.tool;

import com.acme.assistant.model.Customer;
import com.acme.assistant.service.CustomerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class CustomerTools {

    private final CustomerService customerService;

    public CustomerTools(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Tool(description = "Find a customer record by enterprise customer id.")
    public Customer findCustomer(String customerId) {
        return customerService.findCustomer(customerId);
    }
}
