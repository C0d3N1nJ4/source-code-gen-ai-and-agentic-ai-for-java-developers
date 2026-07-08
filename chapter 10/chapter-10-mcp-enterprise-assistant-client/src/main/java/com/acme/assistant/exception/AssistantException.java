package com.acme.assistant.exception;

public class AssistantException extends RuntimeException {

    public AssistantException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssistantException(String message) {
        super(message);
    }
}
