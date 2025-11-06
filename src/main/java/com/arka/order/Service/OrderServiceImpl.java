package com.arka.order.Service;

import com.arka.order.Dto.*;
import com.arka.order.Dto.Response.ApiResponseCreateOrder;
import com.arka.order.Dto.Response.ApiResponseListOrdersByStatus;
import com.arka.order.Entity.Order;
import com.arka.order.Entity.OrderItem;
import com.arka.order.Entity.OrderStockReservation;
import com.arka.order.Repository.OrderItemRepository;
import com.arka.order.Repository.OrderRepository;
import com.arka.order.Repository.OrderStockReservationRepository;
import com.arka.order.Utils.Errors.BusinessException;
import com.arka.order.Utils.OrderStatus;
import com.arka.order.Utils.ReservationStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService{
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final OrderItemRepository orderItemRepository;
    private final OrderStockReservationRepository orderStockReservation;

    public OrderServiceImpl (OrderRepository orderRepository,
                             OrderStockReservationRepository orderStockReservation,
                             OrderItemRepository orderItemRepository){
        this.orderRepository = orderRepository;
        this.webClient = WebClient.create("https://64474k7pgh.execute-api.us-east-2.amazonaws.com/dev/inventory");
        this.orderStockReservation = orderStockReservation;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public ApiResponseCreateOrder<OrderResponse> createOrder(CreateOrderRequest request){
        try{
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
                                                    new BusinessException("Error en servicio de inventario: "+error)
                                            ))).bodyToMono(ProductStockValidationResponse.class)
                            .block();

            if (stockResponse == null) {
                throw new BusinessException("No se recibió respuesta del microservicio de productos");
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
                return ApiResponseCreateOrder.<OrderResponse>builder()
                        .code("002")
                        .message("Los siguientes productos no tienen stock disponible: "+ ids)
                        .body(null)
                        .build();
            }

            for (OrderItemRequest item : request.getItems()) {
                ResponseCheckIfStock stockInfo = found.stream()
                        .filter(p -> p.getProductId().equals(item.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(
                                "No se encontró información de inventario para el producto " + item.getProductId()
                        ));

                int alreadyReserved = orderStockReservation.getTotalReservedByProduct(item.getProductId());
                int available = stockInfo.getQuantity() - alreadyReserved;

                System.out.println("Habilitado: "+available);

                if (item.getQuantity() > available) {
                    return ApiResponseCreateOrder.<OrderResponse>builder()
                            .code("002")
                            .message("Cantidad solicitada (" + item.getQuantity() +
                                    ") excede el stock disponible (" + available + ") para el producto " + item.getProductId())
                            .body(null)
                            .build();
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

            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("000")
                    .message("Orden creada exitosamente")
                    .body(mapToResponseDTO(order))
                    .build();

        }catch (BusinessException ex){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("100")
                    .message("Error inesperado al crear la orden: "+ex.getMessage())
                    .body(null)
                    .build();
        } catch (Exception e){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("999")
                    .message("Error inesperado al crear la orden: " + e.getMessage())
                    .body(null)
                    .build();
        }
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
                        "numOrder",orderId,
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

    @Transactional
    public ApiResponseCreateOrder<OrderResponse> addItemsOrder(AddProductToOrderRequest request){
        try{
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new BusinessException("No se encontró la orden: "+request.getOrderId()));

            if(order.getStatus() != OrderStatus.PENDIENTE){
                return ApiResponseCreateOrder.<OrderResponse>builder()
                        .code("002")
                        .message("La orden se encuentra en un estado: "+order.getStatus()+" La orden solo se puede" +
                                " adicionar un nuevo producto, si se encuentra en estado: PENDIENTE")
                        .body(null)
                        .build();
            }

            List<RequestCheckIfStock> productIds = request.getItems().stream()
                    .map(i -> new RequestCheckIfStock(i.getProductId()))
                    .toList();

            ProductStockValidationResponse stockResponse = webClient.post()
                    .uri("/api/v1/inventory/check-if-Stock")
                    .bodyValue(productIds)
                    .retrieve()
                    .bodyToMono(ProductStockValidationResponse.class)
                    .block();

            if (stockResponse == null) {
                throw new BusinessException("No se recibió respuesta del microservicio de productos");
            }

            List<ResponseCheckIfStock> found = Optional.ofNullable(stockResponse.getFoundInventories())
                    .orElse(Collections.emptyList());
            List<ResponseCheckIfStock> notFound = Optional.ofNullable(stockResponse.getNotFoundProductStock())
                    .orElse(Collections.emptyList());

            if (!notFound.isEmpty()) {
                String ids = notFound.stream()
                        .map(p -> String.valueOf(p.getProductId()))
                        .collect(Collectors.joining(", "));
                return ApiResponseCreateOrder.<OrderResponse>builder()
                        .code("002")
                        .message("Los siguientes productos no tienen stock disponible: "+ ids)
                        .body(null)
                        .build();
            }

            List<OrderItem> newItems = new ArrayList<>();
            for (OrderItemRequest item : request.getItems()) {
                ResponseCheckIfStock stockInfo = found.stream()
                        .filter(p -> p.getProductId().equals(item.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(
                                "No se encontró información de inventario para el producto " + item.getProductId()
                        ));

                int alreadyReserved = orderStockReservation.getTotalReservedByProduct(item.getProductId());
                int available = stockInfo.getQuantity() - alreadyReserved;

                System.out.println("Habilitado: "+available);

                if (item.getQuantity() > available) {
                    return ApiResponseCreateOrder.<OrderResponse>builder()
                            .code("002")
                            .message("Cantidad solicitada (" + item.getQuantity() +
                                    ") excede el stock disponible (" + available + ") para el producto " + item.getProductId())
                            .body(null)
                            .build();
                }

                OrderItem newItem = new OrderItem();
                newItem.setOrder(order);
                newItem.setProductId(item.getProductId());
                newItem.setQuantity(item.getQuantity());
                newItem.setPrice(item.getPrice());
                newItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                newItems.add(newItem);

                OrderStockReservation reservation = new OrderStockReservation();
                reservation.setOrderId(order.getIdOrder());
                reservation.setProductId(item.getProductId());
                reservation.setReservedQuantity(item.getQuantity());
                reservation.setCreatedAt(LocalDateTime.now());
                reservation.setStatus(ReservationStatus.PENDIENTE);

                orderStockReservation.save(reservation);
            }

            order.getItems().addAll(newItems);

            BigDecimal total = order.getItems().stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(total);

            orderRepository.save(order);

            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("000")
                    .message("Producto agregado a la orden exitosamente")
                    .body(mapToResponseDTO(order))
                    .build();

        }catch (BusinessException ex){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("100")
                    .message("Error inesperado al adicionar un nuevo producto: "+ex.getMessage())
                    .body(null)
                    .build();
        } catch (Exception e){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("999")
                    .message("Error inesperado al adicionar un nuevo producto: " + e.getMessage())
                    .body(null)
                    .build();
        }
    }

    @Transactional
    public ApiResponseCreateOrder<OrderResponse> removeProducToOrder(Long orderId,DeleteProductToOrderRequest request){
        try{
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new BusinessException("Orden no encontrada con ID: " + orderId));

            if (!order.getStatus().equals(OrderStatus.PENDIENTE)) {
                throw new BusinessException("Solo se pueden eliminar productos de órdenes en estado PENDIENTE");
            }

            List<Long> productIdsToRemove = request.getProductId();

            // Filtramos los items que pertenecen a la orden
            List<OrderItem> itemsToRemove = order.getItems().stream()
                    .filter(item -> productIdsToRemove.contains(item.getProductId()))
                    .collect(Collectors.toList());

            if (itemsToRemove.isEmpty()) {
                throw new BusinessException("Ninguno de los productos especificados existe en la orden");
            }

            order.getItems().removeAll(itemsToRemove);
            orderItemRepository.deleteAll(itemsToRemove);

            List<OrderStockReservation> reservationsToDelete =
                    orderStockReservation.findByOrderIdAndProductIdIn(orderId, request.getProductId());

            if (!reservationsToDelete.isEmpty()) {
                orderStockReservation.deleteAll(reservationsToDelete);
            }

            BigDecimal newTotal = order.getItems().stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(newTotal);

            orderRepository.save(order);

            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("000")
                    .message("Producto eliminado de la orden exitosamente")
                    .body(mapToResponseDTO(order))
                    .build();

        }catch (BusinessException ex){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("100")
                    .message("Error inesperado al adicionar un nuevo producto: "+ex.getMessage())
                    .body(null)
                    .build();
        } catch (Exception e){
            return ApiResponseCreateOrder.<OrderResponse>builder()
                    .code("999")
                    .message("Error inesperado al adicionar un nuevo producto: " + e.getMessage())
                    .body(null)
                    .build();
        }
    }

    public ApiResponseListOrdersByStatus getOrdersByStatus(String status){
        OrderStatus orderStatus;
        try{
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BusinessException("Estado de orden no válido: " + status);
        }

        List<Order> orders = orderRepository.findByStatus(orderStatus);

        if (orders.isEmpty()) {
            throw new BusinessException("No se encontraron órdenes con estado: " + status);
        }

        List<OrderResponse> responseList = orders.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ApiResponseListOrdersByStatus.builder()
                .success(true)
                .message("Órdenes obtenidas exitosamente con estado: " + status)
                .orders(responseList)
                .build();
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
