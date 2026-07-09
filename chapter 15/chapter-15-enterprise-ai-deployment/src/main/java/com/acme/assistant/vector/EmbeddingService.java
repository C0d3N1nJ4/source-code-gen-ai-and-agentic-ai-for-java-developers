package com.acme.assistant.vector;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    public List<Double> embed(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        List<Double> vector = new ArrayList<>();
        for (byte value : bytes) {
            vector.add((double) (value & 0xFF) / 255.0);
            if (vector.size() == 16) {
                break;
            }
        }
        while (vector.size() < 16) {
            vector.add(0.0);
        }
        return vector;
    }
}
