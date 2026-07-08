package com.acme.assistant.chapter12.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "acme.security")
public class AiSecurityProperties {

    private final Jwt jwt = new Jwt();
    private final Prompt prompt = new Prompt();
    private final Memory memory = new Memory();
    private final Mcp mcp = new Mcp();
    private final Governance governance = new Governance();

    public Jwt getJwt() {
        return jwt;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public Memory getMemory() {
        return memory;
    }

    public Mcp getMcp() {
        return mcp;
    }

    public Governance getGovernance() {
        return governance;
    }

    public static class Jwt {
        @NotBlank
        private String secret = "chapter12-secret-key-for-jwt-signing-123456";

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class Prompt {
        @Min(1)
        @Max(100)
        private int blockThreshold = 60;

        @Min(10)
        @Max(100000)
        private int maxPromptLength = 8000;

        public int getBlockThreshold() {
            return blockThreshold;
        }

        public void setBlockThreshold(int blockThreshold) {
            this.blockThreshold = blockThreshold;
        }

        public int getMaxPromptLength() {
            return maxPromptLength;
        }

        public void setMaxPromptLength(int maxPromptLength) {
            this.maxPromptLength = maxPromptLength;
        }
    }

    public static class Memory {
        @Min(1)
        @Max(3650)
        private int retentionDays = 180;

        @Min(1)
        @Max(1000)
        private int contextWindowSize = 12;

        public int getRetentionDays() {
            return retentionDays;
        }

        public void setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
        }

        public int getContextWindowSize() {
            return contextWindowSize;
        }

        public void setContextWindowSize(int contextWindowSize) {
            this.contextWindowSize = contextWindowSize;
        }
    }

    public static class Mcp {
        @Min(50)
        @Max(60000)
        private int timeoutMillis = 3000;

        @Min(0)
        @Max(10)
        private int retries = 2;

        public int getTimeoutMillis() {
            return timeoutMillis;
        }

        public void setTimeoutMillis(int timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }
    }

    public static class Governance {
        @Min(1)
        @Max(1000000)
        private int autoApproveExpenseLimit = 500;

        public int getAutoApproveExpenseLimit() {
            return autoApproveExpenseLimit;
        }

        public void setAutoApproveExpenseLimit(int autoApproveExpenseLimit) {
            this.autoApproveExpenseLimit = autoApproveExpenseLimit;
        }
    }
}
