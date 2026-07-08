package com.acme.assistant.chapter12.entities;

import com.acme.assistant.chapter12.exceptions.BadRequestException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static final String KEY = "acme-chapter-12-memory-key";

    @Override
    public String convertToDatabaseColumn(String value) {
        if (value == null) {
            return null;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] key = KEY.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            encrypted[i] = (byte) (bytes[i] ^ key[i % key.length]);
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(value);
            byte[] key = KEY.getBytes(StandardCharsets.UTF_8);
            byte[] decrypted = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                decrypted[i] = (byte) (bytes[i] ^ key[i % key.length]);
            }
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unable to decrypt stored value");
        }
    }
}
