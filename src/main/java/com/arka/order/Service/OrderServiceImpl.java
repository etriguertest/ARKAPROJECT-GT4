package com.arka.order.Service;

import com.arka.order.Dto.*;
import com.arka.order.Entity.Order;
import com.arka.order.Entity.OrderItem;
import com.arka.order.Entity.OrderStockReservation;
import com.arka.order.Repository.OrderRepository;
import com.arka.order.Repository.OrderStockReservationRepository;
import com.arka.order.Utils.OrderStatus;
import com.arka.order.Utils.ReservationStatus;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService{
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final OrderStockReservationRepository orderStockReservation;

    public OrderServiceImpl (OrderRepository orderRepository, WebClient webClient,
                             OrderStockReservationRepository orderStockReservation){
        this.orderRepository = orderRepository;
        this.webClient = webClient;
        this.orderStockReservation = orderStockReservation;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDIENTE);
        order.setOrderDate(LocalDateTime.now());

        List<RequestCheckIfStock> productId = request.getItems().stream().
                map(item -> new RequestCheckIfStock(item.getProductId())).toList();

        System.out.println("listado de productos: "+productId);

        ProductStockValidationResponse stockResponse =
                webClient.post()
                .uri("/api/v1/inventory/check-if-Stock")
                .bodyValue(productId)
                .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(error -> Mono.error(
                                                new RuntimeException("Error en servicio de inventario: "+error)
                                        ))).bodyToMono(ProductStockValidationResponse.class)
                        .block();

        if (stockResponse == null) {
            throw new RuntimeException("No se recibió respuesta del microservicio de productos");
        }

        List<ResponseCheckIfStock> found = Optional.ofNullable(stockResponse.getFoundInventories())
                .orElse(Collections.emptyList());
        List<ResponseCheckIfStock> notFound = Optional.ofNullable(stockResponse.getNotFoundProductStock())
                .orElse(Collections.emptyList());

        System.out.println(found);
        System.out.println(notFound);

        if (!notFound.isEmpty()) {
            String ids = notFound.stream()
                    .map(p -> String.valueOf(p.getProductId()))
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Los siguientes productos no tienen stock: " + ids);
        }

        for (OrderItemRequest item : request.getItems()) {
            ResponseCheckIfStock stockInfo = found.stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró información de inventario para el producto " + item.getProductId()
                    ));

            int alreadyReserved = orderStockReservation.getTotalReservedByProduct(item.getProductId());
            int available = stockInfo.getQuantity() - alreadyReserved;

            System.out.println("Habilitado: "+available);

            if (item.getQuantity() > available) {
                throw new RuntimeException("Cantidad solicitada (" + item.getQuantity() +
                        ") excede el stock disponible (" + stockInfo.getQuantity() + ") para el producto " + item.getProductId());
            }
        }

        List<OrderItem> items = request.getItems().stream().map(itemDto ->{
        OrderItem item = new OrderItem();
        item.setProductId(itemDto.getProductId());
        item.setQuantity(itemDto.getQuantity());
        item.setPrice(itemDto.getPrice());
        item.setSubtotal(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        item.setOrder(order);
        return item;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        order.setItems(items);

        orderRepository.save(order);

        for (OrderItem item : items) {
            OrderStockReservation reservation = new OrderStockReservation();
            reservation.setOrderId(order.getIdOrder());
            reservation.setProductId(item.getProductId());
            reservation.setReservedQuantity(item.getQuantity());
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setStatus(ReservationStatus.PENDIENTE);
            orderStockReservation.save(reservation);
        }

        return mapToResponseDTO(order);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada "+ orderId));

        if(order.getStatus() != OrderStatus.PENDIENTE){
            throw new RuntimeException("La orden ya se encuentra confirmada.");
        }

        List<OrderStockReservation> reservations = orderStockReservation.findByOrderId(orderId);

        if(reservations.isEmpty()){
            throw new RuntimeException("No se encontraron asociadas reservaciones");
        }

        List<Map<String, Object>> reduceStockRequest = reservations.stream()
                .map(r -> Map.of(
                        "type",0,
                        "productId", (Object) r.getProductId(),
                        "quantity", (Object) r.getReservedQuantity()
                ))
                .toList();

        ResponseReduceQuantities stockReduced = webClient.post()
                .uri("/api/v1/inventory/update-quantities")
                .bodyValue(reduceStockRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(
                                        new RuntimeException("Error reduciendo stock: " + error)
                                )))
                .bodyToMono(ResponseReduceQuantities.class)
                .block();

        if (stockReduced == null || !"000".equals(stockReduced.getCodResponse())) {
            throw new RuntimeException("Error al reducir el stock real: " +
                    (stockReduced != null ? stockReduced.getMesssage() : "sin respuesta"));
        }

        order.setStatus(OrderStatus.CONFIRMADO);
        order.setConfirmationDate(LocalDateTime.now());
        orderRepository.save(order);

        for(OrderStockReservation reservation : reservations){
            reservation.setStatus(ReservationStatus.CONFIRMADO);
            orderStockReservation.save(reservation);
        }

        return mapToResponseDTO(order);
    }

    private OrderResponse mapToResponseDTO(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setIdOrder(order.getIdOrder());
        dto.setCustomerId(order.getCustomerId());
        dto.setStatus(order.getStatus().name());
        dto.setOrderDate(order.getOrderDate());
        dto.setConfirmationDate(order.getConfirmationDate());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderItemRequest> itemsDTO = order.getItems().stream().map(item -> {
            OrderItemRequest i = new OrderItemRequest();
            i.setProductId(item.getProductId());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemsDTO);
        return dto;
    }
}
