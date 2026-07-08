package com.acme.assistant.model;

public record RemoteToolDescriptor(
        String name,
        String description,
        String inputSchema) {
}
