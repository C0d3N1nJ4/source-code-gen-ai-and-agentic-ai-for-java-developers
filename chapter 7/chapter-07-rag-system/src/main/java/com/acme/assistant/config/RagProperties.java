package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties(prefix = "app.rag")
public class RagProperties {

    private int topK = 5;

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        if (topK <= 0) {
            throw new IllegalArgumentException("app.rag.top-k must be greater than zero");
        }
        this.topK = topK;
    }

}
