package com.acme.assistant.chapter12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.acme.assistant.chapter12")
@EnableJpaRepositories(basePackages = "com.acme.assistant.chapter12.repositories")
public class Chapter12AiSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chapter12AiSecurityApplication.class, args);
    }
}
