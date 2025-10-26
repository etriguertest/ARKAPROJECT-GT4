package com.arka.notification.repository;

import com.arka.notification.dto.WebhookRequestDto;
import reactor.core.publisher.Mono;

public interface N8nWebhookService {
    Mono<String> callN8nWebhook(WebhookRequestDto data);
}
