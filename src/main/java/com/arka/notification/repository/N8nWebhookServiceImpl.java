package com.arka.notification.repository;
import com.arka.notification.config.KeysConfiguration;
import com.arka.notification.dto.WebhookRequestDto;
import com.arka.notification.queue.SqsTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class N8nWebhookServiceImpl implements N8nWebhookService{

    private final WebClient webClient;
    private final KeysConfiguration configs;

    public N8nWebhookServiceImpl(
            WebClient.Builder webClientBuilder,
//            @Value("${n8n-webhook-url}") String n8nWebhookUrl, // Value is guaranteed to be available
            KeysConfiguration configs
    ) {
        this.webClient = webClientBuilder.baseUrl(configs.getN8nWebhookUrl()).build();
        this.configs = configs;
        // Optional: Log the initialized base URL for confirmation
        System.out.println("WebClient initialized with base URL: " + configs.getN8nWebhookUrl());
    }

    public Mono<String> callN8nWebhook(WebhookRequestDto data) {
        System.out.println("callN8nWebhook -> N8N_WEBHOOK_URL --> "+configs.getN8nWebhookUrl());
        System.out.println("callN8nWebhook -> N8N_WEBHOOK_URL --> "+configs.getQueueNameOrderNotification());
        SqsTransformer sqsTransformer = new SqsTransformer();
//        Mono<String> monoString = sqsTransformer.toString(data);
//        requestBody = monoString.block();
        System.out.println("About to send requestToN8N--> "+data);

        webClient.post()
                .uri("/") // Since the base URL is set in the constructor, this is the full path.
//                .body(BodyInserters.fromValue(requestBody))
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class) // Expecting a String response (e.g., "Workflow got started")
                .doOnSuccess(response -> {
                    System.out.println("n8n webhook call successful. Response: " + response);
                })
                .onErrorResume(e -> {
                    System.err.println("Error calling n8n webhook: " + e.getMessage());
                    return Mono.just("Error: " + e.getMessage());
                }).subscribe();
        return Mono.just("Exitoso");
    }
}