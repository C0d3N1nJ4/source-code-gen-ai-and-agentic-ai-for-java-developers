package com.acme.assistant.rag;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.observability.AiMetricsService;
import com.acme.assistant.vector.InMemoryVectorRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RagServiceTest {

    @Test
    void shouldReturnTopCitation() {
        InMemoryVectorRepository repository = new InMemoryVectorRepository();
        repository.ingest("doc-1", "HR leave policy allows 20 days", Map.of("domain", "hr"));
        repository.ingest("doc-2", "Finance policy for expenses", Map.of("domain", "finance"));

        RagService ragService = new RagService(repository, new AiMetricsService(new SimpleMeterRegistry()));
        RagService.RagResult result = ragService.query("leave policy");

        assertThat(result.citations()).contains("doc-1");
    }
}
