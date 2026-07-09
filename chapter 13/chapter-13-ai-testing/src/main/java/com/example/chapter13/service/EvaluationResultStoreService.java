package com.example.chapter13.service;

import com.example.chapter13.config.AiEvaluationStorageProperties;
import com.example.chapter13.dto.EvaluationResponses.FullEvaluationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class EvaluationResultStoreService {

    private final ObjectMapper objectMapper;
    private final AiEvaluationStorageProperties storageProperties;

    public EvaluationResultStoreService(ObjectMapper objectMapper, AiEvaluationStorageProperties storageProperties) {
        this.objectMapper = objectMapper;
        this.storageProperties = storageProperties;
    }

    public void persist(FullEvaluationResult result) {
        try {
            Path jsonPath = Path.of(storageProperties.resultJsonPath());
            Path htmlPath = Path.of(storageProperties.resultHtmlPath());
            Files.createDirectories(jsonPath.getParent());
            Files.createDirectories(htmlPath.getParent());

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), result);
            Files.writeString(htmlPath, toHtml(result), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to persist evaluation results", ex);
        }
    }

    public FullEvaluationResult readLatest() {
        Path jsonPath = Path.of(storageProperties.resultJsonPath());
        if (!Files.exists(jsonPath)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonPath.toFile(), FullEvaluationResult.class);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read evaluation results", ex);
        }
    }

    private String toHtml(FullEvaluationResult result) {
        String status = result.qualityReport().overallStatus();
        return """
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                  <meta charset=\"utf-8\" />
                  <title>Acme AI Evaluation Report</title>
                  <style>
                    body { font-family: 'IBM Plex Sans', sans-serif; background: #f5f6f8; color: #102a43; margin: 2rem; }
                    .card { background: white; border-radius: 12px; padding: 1rem 1.2rem; margin-bottom: 1rem; box-shadow: 0 8px 18px rgba(16,42,67,0.08); }
                    h1, h2 { margin: 0.2rem 0 0.8rem; }
                    .status { font-weight: 700; color: %s; }
                    table { width: 100%%; border-collapse: collapse; }
                    th, td { text-align: left; border-bottom: 1px solid #d9e2ec; padding: 0.5rem; }
                  </style>
                </head>
                <body>
                  <div class=\"card\">
                    <h1>Acme Enterprise Assistant AI Evaluation</h1>
                    <p>Result ID: %s</p>
                    <p>Overall Status: <span class=\"status\">%s</span></p>
                  </div>
                  <div class=\"card\">
                    <h2>Quality Gates</h2>
                    <table>
                      <thead><tr><th>Gate</th><th>Actual</th><th>Threshold</th><th>Status</th></tr></thead>
                      <tbody>
                        %s
                      </tbody>
                    </table>
                  </div>
                </body>
                </html>
                """.formatted(
                "READY".equals(status) ? "#127a45" : "#9f1239",
                result.resultId(),
                status,
                result.qualityReport().gates().stream()
                        .map(gate -> "<tr><td>%s</td><td>%d</td><td>%d</td><td>%s</td></tr>"
                                .formatted(gate.gate(), gate.actual(), gate.threshold(), gate.status()))
                        .reduce("", String::concat));
    }
}
