package com.acme.assistant.security;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PiiRedactor {

    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PHONE = Pattern.compile("\\b\\d{3}[-. ]?\\d{3}[-. ]?\\d{4}\\b");

    public String redact(String input) {
        if (input == null) {
            return "";
        }
        String redacted = EMAIL.matcher(input).replaceAll("[REDACTED_EMAIL]");
        return PHONE.matcher(redacted).replaceAll("[REDACTED_PHONE]");
    }
}
