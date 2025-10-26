package com.arka.notification.controller;

import com.arka.notification.dto.WebhookRequestDto;
import com.arka.notification.repository.N8nWebhookService;
import com.arka.notification.repository.N8nWebhookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final N8nWebhookService sern8nWebhookServicevice;


    @PostMapping("/notif-order-email")
    public Mono<String> sendEmail(@RequestBody WebhookRequestDto filters) {
        return sern8nWebhookServicevice.callN8nWebhook(filters);
//        n8nWebhookService.callN8nWebhook(filters.getTriggerEvent(),filters.getPayloadData());
//        return Mono.just("Correo Enviado");
    }

}
