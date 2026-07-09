package com.acme.assistant.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "assistant")
public class AssistantProperties {

    private final Security security = new Security();
    private final Mcp mcp = new Mcp();
    private final Tools tools = new Tools();
    private final Routing routing = new Routing();
    private final Resilience resilience = new Resilience();

    public static class Security {
        private String bearerToken = "dev-token";

        public String getBearerToken() {
            return bearerToken;
        }

        public void setBearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
        }
    }

    public static class Mcp {
        private String baseUrl = "http://localhost:8081";
        private List<String> allowList = new ArrayList<>();

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public List<String> getAllowList() {
            return allowList;
        }

        public void setAllowList(List<String> allowList) {
            this.allowList = allowList;
        }
    }

    public static class Tools {
        private List<String> allowList = new ArrayList<>();

        public List<String> getAllowList() {
            return allowList;
        }

        public void setAllowList(List<String> allowList) {
            this.allowList = allowList;
        }
    }

    public static class Routing {
        private String localSmallModel = "llama3.1:8b";
        private String defaultModel = "qwen3:8b";
        private String reasoningModel = "deepseek-r1:14b";
        private String codingModel = "qwen2.5-coder:7b";
        private String cloudFallbackModel = "gpt-4.1-mini";

        public String getLocalSmallModel() {
            return localSmallModel;
        }

        public void setLocalSmallModel(String localSmallModel) {
            this.localSmallModel = localSmallModel;
        }

        public String getDefaultModel() {
            return defaultModel;
        }

        public void setDefaultModel(String defaultModel) {
            this.defaultModel = defaultModel;
        }

        public String getReasoningModel() {
            return reasoningModel;
        }

        public void setReasoningModel(String reasoningModel) {
            this.reasoningModel = reasoningModel;
        }

        public String getCodingModel() {
            return codingModel;
        }

        public void setCodingModel(String codingModel) {
            this.codingModel = codingModel;
        }

        public String getCloudFallbackModel() {
            return cloudFallbackModel;
        }

        public void setCloudFallbackModel(String cloudFallbackModel) {
            this.cloudFallbackModel = cloudFallbackModel;
        }
    }

    public static class Resilience {
        private long timeoutMs = 30_000;
        private int maxRetries = 2;

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }

    public Security getSecurity() {
        return security;
    }

    public Mcp getMcp() {
        return mcp;
    }

    public Tools getTools() {
        return tools;
    }

    public Routing getRouting() {
        return routing;
    }

    public Resilience getResilience() {
        return resilience;
    }
}
