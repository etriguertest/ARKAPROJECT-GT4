package com.arka.notification.queue;

import com.arka.notification.dto.WebhookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class SqsTransformer {
    private final ObjectMapper objectMapper;

    /**
     * Initializes the ObjectMapper with support for Java 8 Date/Time types (like LocalDate).
     */
    public SqsTransformer() {
        this.objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
    public Mono<String> toString(WebhookRequestDto dto) {
        return Mono.fromCallable(() -> {
            try {
                // Serialize the DTO object into a JSON string
                // use writeValueAsString for single object serialization
                return objectMapper.writeValueAsString(dto);
            } catch (IOException e) {
                // Handle serialization errors (e.g., circular references, mapping issues)
                throw new IllegalStateException("Failed to serialize DTO to JSON string: " + dto.toString(), e);
            }
        });
    }

}
