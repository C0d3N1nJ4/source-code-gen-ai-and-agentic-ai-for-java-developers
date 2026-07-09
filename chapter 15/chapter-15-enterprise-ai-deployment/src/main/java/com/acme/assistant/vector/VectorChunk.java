package com.acme.assistant.vector;

import java.util.Map;

public record VectorChunk(String id, String content, double score, Map<String, String> metadata) {
}
