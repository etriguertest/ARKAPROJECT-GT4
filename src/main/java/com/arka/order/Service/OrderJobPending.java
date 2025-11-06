package com.arka.order.Service;

import com.arka.order.Dto.Response.CustomerResponse;
import com.arka.order.Entity.Order;
import com.arka.order.Repository.OrderRepository;
import com.arka.order.Utils.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderJobPending {
    private final OrderRepository orderRepository;
    private WebClient webClient;

    public OrderJobPending(OrderRepository orderRepository){
        this.webClient = WebClient.create("https://64474k7pgh.execute-api.us-east-2.amazonaws.com/dev/customer/api/customers/");
    this.orderRepository = orderRepository;
    }

    //Se corre cada 12 horas
    @Scheduled(cron = "0 0 */12 * * *")
    public void checkAbandonOrder(){
        System.out.println("Iniciando job de verificación de órdenes pendientes abandonadas...");
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(1);

        List<Order> pendingOrders = orderRepository.findPendingOrdersOlderThan(OrderStatus.PENDIENTE, threeDaysAgo);

        if (pendingOrders.isEmpty()) {
            System.out.println("No se encontraron órdenes pendientes con más de 3 días.");
            return;
        }

        // Aquí puedes decidir qué hacer con ellas (notificar, cambiar estado, etc.)
        pendingOrders.forEach(order -> {
            System.out.println("Orden pendiente detectada: ID={}, Fecha={}, Estado={}"+
                    order.getIdOrder()+ order.getOrderDate()+ order.getStatus());

            CustomerResponse response = webClient.get()
                    .uri("{id}",order.getCustomerId()).retrieve()
                            .bodyToMono(CustomerResponse.class)
                                    .block();
            if(response !=null){
                System.out.println("el elmail del usuario es: "+response.email);
            }

            // Ejemplo: actualizamos a “ABANDONADA”
            //order.setStatus(OrderStatus.ABANDONADA);
        });

        orderRepository.saveAll(pendingOrders);
        System.out.println("Se actualizaron {} órdenes como ABANDONADAS."+ pendingOrders.size());
    }
}
