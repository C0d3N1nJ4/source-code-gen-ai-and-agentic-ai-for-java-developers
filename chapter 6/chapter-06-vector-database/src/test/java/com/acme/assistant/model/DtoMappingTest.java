package com.acme.assistant.model;

import com.acme.assistant.search.SearchResultMapper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

@DisplayName("DTO Mapping Tests")
class DtoMappingTest {

    @Test
    void mapsVectorDocumentToSearchResultDto() {
        SearchResultMapper mapper = new SearchResultMapper();
        Document document = new Document(
                "Employees may work remotely up to three days per week.",
                Map.of(
                        "chunkId", "chunk-1",
                        "documentId", "remote-work-policy",
                        "chunkIndex", 0,
                        "documentTitle", "Remote Work Policy",
                        "department", "HR",
                        "category", "Remote Work",
                        "source", "acme://policies/hr/remote-work",
                        "score", 0.97));

        SearchResult result = mapper.toSearchResult(document);

        Assertions.assertEquals("chunk-1", result.chunkId());
        Assertions.assertEquals("remote-work-policy", result.documentId());
        Assertions.assertEquals("Remote Work Policy", result.metadata().documentTitle());
        Assertions.assertEquals(0.97, result.similarityScore());
    }
}
