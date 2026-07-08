package com.acme.assistant.search;

import com.acme.assistant.config.SemanticSearchProperties;
import com.acme.assistant.model.SearchResult;
import com.acme.assistant.model.SemanticSearchRequest;
import com.acme.assistant.model.SemanticSearchResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {

    private final VectorStore vectorStore;
    private final SearchResultMapper mapper;
    private final SemanticSearchProperties properties;

    public SemanticSearchService(
            VectorStore vectorStore,
            SearchResultMapper mapper,
            SemanticSearchProperties properties) {
        this.vectorStore = vectorStore;
        this.mapper = mapper;
        this.properties = properties;
    }

    public SemanticSearchResponse search(SemanticSearchRequest request) {
        int topK = request.topK() == null ? properties.topK() : request.topK();
        SearchRequest searchRequest = createSearchRequest(request.query(), topK, request.department(), request.category());

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        if (documents == null) {
            documents = List.of();
        }

        List<SearchResult> results = new ArrayList<>(documents.size());
        for (Document document : documents) {
            results.add(mapper.toSearchResult(document));
        }

        return new SemanticSearchResponse(request.query(), topK, results.size(), results);
    }

    private SearchRequest createSearchRequest(String query, int topK, String department, String category) {
        SearchRequest.Builder builder = SearchRequest.builder().query(query).topK(topK);
        String filterExpression = filterExpression(department, category);
        if (filterExpression != null) {
            builder.filterExpression(filterExpression);
        }
        return builder.build();
    }

    private String filterExpression(String department, String category) {
        List<String> clauses = new ArrayList<>();
        if (department != null && !department.isBlank()) {
            clauses.add("department == '" + sanitize(department) + "'");
        }
        if (category != null && !category.isBlank()) {
            clauses.add("category == '" + sanitize(category) + "'");
        }

        if (clauses.isEmpty()) {
            return null;
        }
        return String.join(" && ", clauses);
    }

    private String sanitize(String value) {
        return value.replace("'", "");
    }
}
