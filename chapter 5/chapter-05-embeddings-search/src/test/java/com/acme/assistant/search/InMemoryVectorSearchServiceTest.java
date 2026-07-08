package com.acme.assistant.search;

import com.acme.assistant.model.DocumentChunk;
import com.acme.assistant.model.EmbeddedDocument;
import com.acme.assistant.model.SearchResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InMemoryVectorSearchService Tests")
class InMemoryVectorSearchServiceTest {

    @Test
    void cosineSimilarityReturnsExpectedValueForAlignedVectors() {
        InMemoryVectorSearchService service = new InMemoryVectorSearchService();

        double similarity = service.cosineSimilarity(
                new float[] {1.0f, 0.0f},
                new float[] {0.8f, 0.2f});

        Assertions.assertTrue(similarity > 0.95);
    }

    @Test
    void semanticSearchRanksMostRelevantChunkFirst() {
        InMemoryVectorSearchService service = new InMemoryVectorSearchService();

        EmbeddedDocument remoteWork = new EmbeddedDocument(
                chunk("remote", "remote-work", "Remote Work Policy", "Employees may work remotely up to three days per week."),
                new float[] {0.98f, 0.10f});
        EmbeddedDocument leave = new EmbeddedDocument(
                chunk("leave", "annual-leave", "Annual Leave Policy", "Employees receive 20 days of leave."),
                new float[] {0.10f, 0.95f});

        service.indexDocuments(List.of(remoteWork, leave));

        List<SearchResult> results = service.semanticSearch(new float[] {1.0f, 0.0f}, 1);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("remote-work", results.getFirst().documentId());
    }

    private DocumentChunk chunk(String chunkId, String docId, String title, String content) {
        return new DocumentChunk(chunkId, docId, title, 0, content, Map.of("department", "HR"));
    }
}
