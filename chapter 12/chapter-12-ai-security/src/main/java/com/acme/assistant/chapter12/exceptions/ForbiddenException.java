package com.acme.assistant.chapter12.exceptions;

public class ForbiddenException extends SecurityException {
    public ForbiddenException(String message) {
        super(message);
    }
}
