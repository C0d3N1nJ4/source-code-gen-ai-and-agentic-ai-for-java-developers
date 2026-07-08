package com.acme.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.prompts")
public class AppPromptProperties {

    private String defaultAudience = "experienced Java developers";
    private String defaultStyle = "concise, practical, enterprise-focused";

    public String getDefaultAudience() {
        return defaultAudience;
    }

    public void setDefaultAudience(String defaultAudience) {
        this.defaultAudience = defaultAudience;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }
}
