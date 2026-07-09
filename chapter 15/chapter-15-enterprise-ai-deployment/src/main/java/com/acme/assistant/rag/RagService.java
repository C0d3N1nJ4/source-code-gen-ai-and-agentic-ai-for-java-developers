package com.acme.assistant.rag;

import com.acme.assistant.observability.AiMetricsService;
import com.acme.assistant.vector.VectorChunk;
import com.acme.assistant.vector.VectorRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private final VectorRepository repository;
    private final AiMetricsService metricsService;

    public RagService(VectorRepository repository, AiMetricsService metricsService) {
        this.repository = repository;
        this.metricsService = metricsService;
    }

    public RagResult query(String question) {
        List<VectorChunk> chunks = repository.search(question, 3);
        metricsService.recordRagDocuments(chunks.size());
        String context = chunks.stream().map(VectorChunk::content).reduce("", (a, b) -> a + "\n" + b).trim();
        List<String> citations = chunks.stream().map(VectorChunk::id).toList();
        return new RagResult(context, citations);
    }

    public record RagResult(String context, List<String> citations) {
    }
}
