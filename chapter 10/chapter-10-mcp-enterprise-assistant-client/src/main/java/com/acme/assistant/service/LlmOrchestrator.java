package com.acme.assistant.service;

public interface LlmOrchestrator {

    String generateResponse(String systemMessage, String userMessage);
}
