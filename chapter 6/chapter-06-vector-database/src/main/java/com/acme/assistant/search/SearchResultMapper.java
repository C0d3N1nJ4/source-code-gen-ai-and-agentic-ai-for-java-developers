package com.acme.assistant.search;

import com.acme.assistant.model.DocumentMetadata;
import com.acme.assistant.model.SearchResult;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

@Component
public class SearchResultMapper {

    public SearchResult toSearchResult(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        return new SearchResult(
                value(metadata, "chunkId"),
                value(metadata, "documentId"),
                intValue(metadata, "chunkIndex"),
                document.getText(),
                new DocumentMetadata(
                        value(metadata, "documentTitle"),
                        value(metadata, "department"),
                        value(metadata, "category"),
                        value(metadata, "source")),
                score(metadata));
    }

    private String value(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        return value == null ? null : value.toString();
    }

    private int intValue(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value.toString());
    }

    private Double score(Map<String, Object> metadata) {
        Object value = metadata.get("score");
        if (value == null) {
            value = metadata.get("similarity");
        }
        if (value == null) {
            value = metadata.get("distance");
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }
}
