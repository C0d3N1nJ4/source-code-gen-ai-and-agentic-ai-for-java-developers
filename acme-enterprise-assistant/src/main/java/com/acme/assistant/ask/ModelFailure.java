package com.acme.assistant.ask;

public final class ModelFailure extends RuntimeException {
    private final String code;

    public ModelFailure(String code) {
        super(code);
        this.code = code;
    }

    public String code() { return code; }
}
