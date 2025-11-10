package com.arka.cart.Utils;

import com.arka.cart.Dto.CustomerResponse;
import com.arka.cart.Entity.Order;
import com.arka.cart.Repository.OrderRepository;
import com.arka.cart.Service.MessageProducerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class CronJobPendingAbandon {
    private final OrderRepository orderRepository;
    private final MessageProducerService messageProducerService;
    private WebClient webClient;

    public CronJobPendingAbandon(OrderRepository orderRepository, MessageProducerService messageProducerService){
        this.webClient = WebClient.create("https://64474k7pgh.execute-api.us-east-2.amazonaws.com/dev/customer/api/customers/");
        this.orderRepository = orderRepository;
        this.messageProducerService = messageProducerService;
    }

    @Scheduled(cron = "0 0 */12 * * *")
    public void checkAbandonOrder() {
        System.out.println("Iniciando job de verificación de órdenes pendientes...");

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Order> pendingOrders = orderRepository.findPendingOrdersOlderThan(OrderStatus.PENDIENTE, twoDaysAgo);

        if (pendingOrders.isEmpty()) {
            System.out.println("No se encontraron órdenes pendientes con más de 2 días.");
            return;
        }

        for (Order order : pendingOrders) {
            long daysSinceOrder = ChronoUnit.DAYS.between(order.getOrderDate(), LocalDateTime.now());

            System.out.printf("Orden detectada: ID=%d, Fecha=%s, Días transcurridos=%d%n",
                    order.getIdOrder(), order.getOrderDate(), daysSinceOrder);

            if (daysSinceOrder == 2) {
                try {
                    CustomerResponse response = webClient.get()
                            .uri("{id}", order.getCustomerId())
                            .retrieve()
                            .bodyToMono(CustomerResponse.class)
                            .timeout(Duration.ofSeconds(15))
                            .onErrorResume(e -> Mono.empty())
                            .block();

                    if (response != null) {
                        String requestToQueue = String.format(
                                "{\"emailCustomer\":\"%s\", \"userName\":\"%s\", \"orderId\":\"%s\", \"statusOrder\":\"%s\"}",
                                response.email, response.name, order.getIdOrder(), order.getStatus()
                        );

                        messageProducerService.send(requestToQueue);
                        System.out.println("Mensaje enviado a SQS: " + requestToQueue);
                    }
                } catch (Exception e) {
                    System.err.println("Error enviando mensaje SQS para la orden " + order.getIdOrder() + ": " + e.getMessage());
                }
            } else if (daysSinceOrder >= 3) {
                order.setStatus(OrderStatus.ABANDONADA);
                System.out.println("Orden marcada como ABANDONADA: " + order.getIdOrder());
            }
        }

        orderRepository.saveAll(
                pendingOrders.stream()
                        .filter(o -> o.getStatus() == OrderStatus.ABANDONADA)
                        .toList()
        );

        System.out.println("Job completado");
    }
}
