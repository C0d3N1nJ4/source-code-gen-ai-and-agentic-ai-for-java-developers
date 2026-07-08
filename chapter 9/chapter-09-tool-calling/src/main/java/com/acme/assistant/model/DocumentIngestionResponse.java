package com.acme.assistant.model;

public record DocumentIngestionResponse(
        int sourceDocuments,
        int chunksCreated) {
}
