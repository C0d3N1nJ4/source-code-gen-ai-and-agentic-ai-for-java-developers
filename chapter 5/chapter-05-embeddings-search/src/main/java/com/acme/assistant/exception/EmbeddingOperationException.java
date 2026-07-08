package com.acme.assistant.exception;

public class EmbeddingOperationException extends RuntimeException {

    public EmbeddingOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
