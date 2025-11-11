package com.arka.order.Service;

import com.arka.order.Dto.Response.ChangeStatusResponse;
import com.arka.order.Dto.Response.CustomerResponse;
import com.arka.order.Entity.Order;
import com.arka.order.Repository.OrderItemRepository;
import com.arka.order.Repository.OrderRepository;
import com.arka.order.Repository.OrderStockReservationRepository;
import com.arka.order.Utils.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Transactional
public class ChangeOrderServiceImpl implements IChangeOrderService{
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final OrderItemRepository orderItemRepository;
    private final OrderStockReservationRepository orderStockReservation;
    private final MessageProducerService messageProducerService;

    public ChangeOrderServiceImpl (OrderRepository orderRepository,
                             OrderStockReservationRepository orderStockReservation,
                             OrderItemRepository orderItemRepository,
                             MessageProducerService messageProducerService){
        this.orderRepository = orderRepository;
        this.webClient = WebClient.create("https://64474k7pgh.execute-api.us-east-2.amazonaws.com/dev/customer/api/customers/");
        this.orderStockReservation = orderStockReservation;
        this.orderItemRepository = orderItemRepository;
        this.messageProducerService = messageProducerService;
    }

    @Transactional
    public Mono<ChangeStatusResponse> moveToDispatchReactive(Long orderId){
        return Mono.fromCallable(() -> orderRepository.findById(orderId))
                .flatMap(optionalOrder -> optionalOrder.map(Mono::just)
                        .orElseGet(() -> Mono.error(new RuntimeException("La orden con ID " + orderId + " no existe."))))
                .flatMap(order -> {
                    if (order.getStatus() == OrderStatus.ENTREGADA) {
                        return Mono.error(new IllegalStateException("La orden ya fue entregada, no puede pasar a despacho."));
                    }

                    if (order.getStatus() != OrderStatus.CONFIRMADO) {
                        return Mono.error(new IllegalStateException("Solo las órdenes en estado CONFIRMADO pueden pasar a EN_DESPACHO."));
                    }

                    order.setStatus(OrderStatus.EN_DESPACHO);
                    orderRepository.save(order);

                    CustomerResponse responseCustomer = webClient.get()
                            .uri("{id}", order.getCustomerId())
                            .retrieve()
                            .bodyToMono(CustomerResponse.class)
                            .timeout(Duration.ofSeconds(15))
                            .onErrorResume(e -> Mono.empty())
                            .block();

                    if (responseCustomer != null) {
                        String requestToQueue = String.format(
                                "{\"emailCustomer\":\"%s\", \"userName\":\"%s\", \"orderId\":\"%s\", \"statusOrder\":\"%s\"}",
                                responseCustomer.email, responseCustomer.name, order.getIdOrder(), order.getStatus()
                        );

                        messageProducerService.send(requestToQueue);
                        System.out.println("Mensaje enviado a SQS: " + requestToQueue);
                    }

                    ChangeStatusResponse response = new ChangeStatusResponse(
                            order.getIdOrder(),
                            "EN DESPACHO",
                            "Se realizó el cambio de estado exitosamente"
                    );

                    return Mono.just(response);
                });
    }

    @Transactional
    public Mono<ChangeStatusResponse> moveDelivered(Long orderId){
        return Mono.fromCallable(() -> orderRepository.findById(orderId))
                .flatMap(optionalOrder -> optionalOrder.map(Mono::just)
                        .orElseGet(() -> Mono.error(new RuntimeException("La orden con ID " + orderId + " no existe."))))
                .flatMap(order -> {
                    if (order.getStatus() == OrderStatus.ENTREGADA) {
                        return Mono.error(new IllegalStateException("La orden ya fue entregada."));
                    }

                    if (order.getStatus() != OrderStatus.CONFIRMADO && order.getStatus() != OrderStatus.EN_DESPACHO) {
                        return Mono.error(new IllegalStateException("Solo las órdenes en estado Confirmado ó En Despacho" +
                                " pueden pasar a estado Entregada."));
                    }

                    order.setStatus(OrderStatus.ENTREGADA);
                    orderRepository.save(order);

                    CustomerResponse responseCustomer = webClient.get()
                            .uri("{id}", order.getCustomerId())
                            .retrieve()
                            .bodyToMono(CustomerResponse.class)
                            .timeout(Duration.ofSeconds(15))
                            .onErrorResume(e -> Mono.empty())
                            .block();

                    if (responseCustomer != null) {
                        String requestToQueue = String.format(
                                "{\"emailCustomer\":\"%s\", \"userName\":\"%s\", \"orderId\":\"%s\", \"statusOrder\":\"%s\"}",
                                responseCustomer.email, responseCustomer.name, order.getIdOrder(), order.getStatus()
                        );

                        messageProducerService.send(requestToQueue);
                        System.out.println("Mensaje enviado a SQS: " + requestToQueue);
                    }

                    ChangeStatusResponse response = new ChangeStatusResponse(
                            order.getIdOrder(),
                            "ENTREGADA",
                            "Se realizó el cambio de estado exitosamente"
                    );

                    return Mono.just(response);
                });
    }


    public void sendEmailToConfirm(Order order){
        try{
            CustomerResponse responseCustomer = webClient.get()
                    .uri("{id}", order.getCustomerId())
                    .retrieve()
                    .bodyToMono(CustomerResponse.class)
                    .timeout(Duration.ofSeconds(15))
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if (responseCustomer != null) {
                String requestToQueue = String.format(
                        "{\"emailCustomer\":\"%s\", \"userName\":\"%s\", \"orderId\":\"%s\", \"statusOrder\":\"%s\"}",
                        responseCustomer.email, responseCustomer.name, order.getIdOrder(), order.getStatus()
                );

                messageProducerService.send(requestToQueue);
                System.out.println("Mensaje enviado a SQS: " + requestToQueue);
            }

        }catch (Exception e){
            System.out.println("Se presentó una excepción: "+e.getMessage());
        }
    }

    public long getAbandonedOrderCount(){
        return orderRepository.countByStatus(OrderStatus.ABANDONADA);
    }

    public long getOrdersCountNotAbandoned(){
        return orderRepository.countByStatusNot(OrderStatus.ABANDONADA);
    }
}
