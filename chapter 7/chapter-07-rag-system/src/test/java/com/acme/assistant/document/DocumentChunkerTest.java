package com.acme.assistant.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.assistant.model.EnterpriseDocument;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class DocumentChunkerTest {

    private final DocumentChunker documentChunker = new DocumentChunker();

    @Test
    void splitsDocumentIntoMeaningfulChunksWithMetadata() {
        EnterpriseDocument enterpriseDocument = new EnterpriseDocument(
                "IT Security Policy",
                "it-security-policy",
                "Employees must use VPN at all times when on public Wi-Fi and must use company-managed devices. "
                        + "MFA is mandatory for all remote access to internal systems.",
                Map.of("policyType", "security"));

        List<Document> chunks = documentChunker.chunk(enterpriseDocument, 70);

        assertThat(chunks.size()).isGreaterThan(1);
        assertThat(chunks.getFirst().getMetadata()).containsEntry("source", "it-security-policy");
        assertThat(chunks.getFirst().getMetadata()).containsEntry("title", "IT Security Policy");
        assertThat(chunks.getFirst().getMetadata()).containsEntry("chunkIndex", 1);
        assertThat(chunks.getFirst().getMetadata()).containsEntry("policyType", "security");
    }

}
