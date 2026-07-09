package com.acme.assistant.vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryVectorRepository implements VectorRepository {

    private final Map<String, VectorChunk> chunks = new ConcurrentHashMap<>();

    @Override
    public void ingest(String id, String content, Map<String, String> metadata) {
        chunks.put(id, new VectorChunk(id, content, 0.0, metadata));
    }

    @Override
    public List<VectorChunk> search(String query, int limit) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return chunks.values().stream()
            .map(chunk -> {
                long sharedWords = List.of(normalized.split("\\\\s+")).stream()
                    .filter(word -> chunk.content().toLowerCase(Locale.ROOT).contains(word))
                    .count();
                return new VectorChunk(chunk.id(), chunk.content(), sharedWords, chunk.metadata());
            })
            .sorted(Comparator.comparingDouble(VectorChunk::score).reversed())
            .limit(limit)
            .toList();
    }
}
