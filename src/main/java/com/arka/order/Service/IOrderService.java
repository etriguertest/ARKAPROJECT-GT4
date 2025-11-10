package com.arka.order.Service;

import com.arka.order.Dto.AddProductToOrderRequest;
import com.arka.order.Dto.CreateOrderRequest;
import com.arka.order.Dto.DeleteProductToOrderRequest;
import com.arka.order.Dto.OrderResponse;
import com.arka.order.Dto.Response.ApiResponseCreateOrder;
import com.arka.order.Dto.Response.ApiResponseListOrdersByStatus;
import com.arka.order.Dto.Response.ChangeStatusResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IOrderService {
    ApiResponseCreateOrder<OrderResponse> createOrder(CreateOrderRequest request);
    OrderResponse confirmOrder(Long orderId);
    ApiResponseCreateOrder<OrderResponse> addItemsOrder(AddProductToOrderRequest request);
    ApiResponseCreateOrder<OrderResponse> removeProducToOrder(Long orderId, DeleteProductToOrderRequest request);
    ApiResponseListOrdersByStatus getOrdersByStatus(String status);
}
