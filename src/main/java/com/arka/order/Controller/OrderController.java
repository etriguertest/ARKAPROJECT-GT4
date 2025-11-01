package com.arka.order.Controller;

import com.arka.order.Dto.CreateOrderRequest;
import com.arka.order.Dto.OrderResponse;
import com.arka.order.Service.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private IOrderService orderService;

    public OrderController(IOrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request){
        return orderService.createOrder(request);
    }

    @PutMapping("/confirm/{OrderId}")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long OrderId){
        OrderResponse response = orderService.confirmOrder(OrderId);
        return ResponseEntity.ok(response);
    }
}
