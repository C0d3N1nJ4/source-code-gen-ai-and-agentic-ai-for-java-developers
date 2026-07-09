package com.example.chapter13.model;

import java.util.Set;

public record EnterpriseKnowledgeDocument(
        String documentId,
        String domain,
        String title,
        String excerpt,
        Set<String> keywords) {
}