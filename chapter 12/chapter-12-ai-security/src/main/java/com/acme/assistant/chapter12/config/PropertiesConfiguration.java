package com.acme.assistant.chapter12.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiSecurityProperties.class)
public class PropertiesConfiguration {
}
