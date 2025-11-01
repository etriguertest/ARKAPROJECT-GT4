package com.arka.order.Service;

import com.arka.order.Dto.CreateOrderRequest;
import com.arka.order.Dto.OrderResponse;
import org.springframework.http.ResponseEntity;

public interface IOrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse confirmOrder(Long orderId);
}
