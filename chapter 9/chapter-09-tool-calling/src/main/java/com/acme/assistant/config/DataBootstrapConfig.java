package com.acme.assistant.config;

import com.acme.assistant.entity.CustomerEntity;
import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.repository.CustomerRepository;
import com.acme.assistant.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBootstrapConfig {

    @Bean
    CommandLineRunner seedReferenceData(EmployeeRepository employeeRepository, CustomerRepository customerRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
            employeeRepository.save(new EmployeeEntity(
                "EMP-1001",
                "Sarah Connor",
                "sarah.connor@acme.com",
                "EMP-2001",
                14));
            employeeRepository.save(new EmployeeEntity(
                "EMP-1002",
                "John Carter",
                "john.carter@acme.com",
                "EMP-2001",
                9));
            }

            if (customerRepository.count() == 0) {
            customerRepository.save(new CustomerEntity(
                "ACME-1024",
                "Acme Retail North",
                "GOLD",
                "ACTIVE"));
            customerRepository.save(new CustomerEntity(
                "ACME-2048",
                "Acme Logistics West",
                "SILVER",
                "ACTIVE"));
            }
        };
    }
}
