package com.acme.assistant.vector;

import java.util.List;
import java.util.Map;

public interface VectorRepository {

    void ingest(String id, String content, Map<String, String> metadata);

    List<VectorChunk> search(String query, int limit);
}
