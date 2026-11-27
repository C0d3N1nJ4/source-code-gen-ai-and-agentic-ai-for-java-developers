package com.acme.examples.ch03;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Chapter03Application {
    public static void main(String[] args) {
        SpringApplication.run(Chapter03Application.class, args);
    }

    @Bean
    PromptExample promptExample(ChatClient.Builder builder) {
        return new PromptExample(builder.build());
    }

    @Bean
    ApplicationRunner demonstrate(PromptExample example) {
        return args -> System.out.println(example.ask("Explain Java records in two sentences."));
    }
}
