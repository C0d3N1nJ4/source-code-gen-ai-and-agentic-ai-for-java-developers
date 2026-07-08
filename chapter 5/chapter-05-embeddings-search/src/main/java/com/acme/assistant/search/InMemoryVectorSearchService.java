package com.acme.assistant.search;

import com.acme.assistant.model.EmbeddedDocument;
import com.acme.assistant.model.SearchResult;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class InMemoryVectorSearchService {

    private final CopyOnWriteArrayList<EmbeddedDocument> index = new CopyOnWriteArrayList<>();

    public void indexDocuments(List<EmbeddedDocument> embeddedDocuments) {
        index.addAll(embeddedDocuments);
    }

    public List<SearchResult> semanticSearch(float[] queryEmbedding, int topK) {
        int safeTopK = Math.max(1, topK);

        return index.stream()
                .map(document -> toSearchResult(document, cosineSimilarity(queryEmbedding, document.embedding())))
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(safeTopK)
                .toList();
    }

    public int totalIndexedChunks() {
        return index.size();
    }

    public double cosineSimilarity(float[] left, float[] right) {
        int length = Math.min(left.length, right.length);
        double dot = 0.0;
        double leftNorm = 0.0;
        double rightNorm = 0.0;

        for (int i = 0; i < length; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }

        if (leftNorm == 0.0 || rightNorm == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }

    private SearchResult toSearchResult(EmbeddedDocument document, double score) {
        return new SearchResult(
                document.chunk().chunkId(),
                document.chunk().documentId(),
                document.chunk().documentTitle(),
                document.chunk().chunkIndex(),
                score,
                document.chunk().content(),
                Map.copyOf(document.chunk().metadata()));
    }
}
