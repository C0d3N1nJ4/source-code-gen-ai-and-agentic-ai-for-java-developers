package com.acme.assistant.rag;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.model.RetrievedDocumentChunk;
import com.acme.assistant.model.SourceDocument;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SourceDocumentMapperTest {

    private final SourceDocumentMapper mapper = new SourceDocumentMapper();

    @Test
    void mapsAndDeduplicatesSourceDocuments() {
        List<RetrievedDocumentChunk> chunks = List.of(
                new RetrievedDocumentChunk(
                        "c1",
                        "Employees may work remotely up to three days per week.",
                        "remote-work-policy",
                        "Remote Work Policy",
                        1,
                        Map.of()),
                new RetrievedDocumentChunk(
                        "c2",
                        "Employees may work remotely up to three days per week.",
                        "remote-work-policy",
                        "Remote Work Policy",
                        1,
                        Map.of()),
                new RetrievedDocumentChunk(
                        "c3",
                        "International expense claims require receipts and conversion.",
                        "expense-claim-policy",
                        "Expense Claim Policy",
                        2,
                        Map.of()));

        List<SourceDocument> sources = mapper.map(chunks);

        assertThat(sources).hasSize(2);
        assertThat(sources.getFirst().source()).isEqualTo("remote-work-policy");
        assertThat(sources.get(1).source()).isEqualTo("expense-claim-policy");
        assertThat(sources.getFirst().snippet()).isNotBlank();
    }

}
