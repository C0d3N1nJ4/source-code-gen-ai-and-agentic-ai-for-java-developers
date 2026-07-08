package com.acme.assistant.model;

public record Customer(
        String customerId,
        String name,
        String tier,
        String status) {
}
