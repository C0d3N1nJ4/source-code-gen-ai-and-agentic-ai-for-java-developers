package com.example.chapter13;

import com.example.chapter13.config.AiEvaluationStorageProperties;
import com.example.chapter13.config.AiQualityProperties;
import com.example.chapter13.config.AiSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AiQualityProperties.class, AiEvaluationStorageProperties.class, AiSecurityProperties.class})
public class Chapter13AiTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chapter13AiTestingApplication.class, args);
    }
}