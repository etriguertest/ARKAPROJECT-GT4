package com.arka.order.Controller;

import com.arka.order.Dto.AddProductToOrderRequest;
import com.arka.order.Dto.CreateOrderRequest;
import com.arka.order.Dto.DeleteProductToOrderRequest;
import com.arka.order.Dto.OrderResponse;
import com.arka.order.Dto.Response.ApiResponseCreateOrder;
import com.arka.order.Dto.Response.ApiResponseListOrdersByStatus;
import com.arka.order.Dto.Response.ChangeStatusResponse;
import com.arka.order.Service.IChangeOrderService;
import com.arka.order.Service.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {
    private IOrderService orderService;
    private IChangeOrderService changeOrderService;

    public OrderController(IOrderService orderService,IChangeOrderService changeOrderService){
        this.changeOrderService = changeOrderService;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseCreateOrder<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request){
        ApiResponseCreateOrder<OrderResponse> response = orderService.createOrder(request);

        if(!response.getCode().equals("000")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/confirm/{OrderId}")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long OrderId){
        OrderResponse response = orderService.confirmOrder(OrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-product/{orderId}")
    public ResponseEntity<ApiResponseCreateOrder<OrderResponse>> addProductToOrder(
            @PathVariable Long orderId, @Valid @RequestBody AddProductToOrderRequest request
            ){
        request.setOrderId(orderId);

        ApiResponseCreateOrder<OrderResponse> response = orderService.addItemsOrder(request);
        if(!response.getCode().equals("000")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product-order/{orderId}")
    public ResponseEntity<ApiResponseCreateOrder<OrderResponse>> removeProductoToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody DeleteProductToOrderRequest request
            ){
        ApiResponseCreateOrder<OrderResponse> response = orderService.removeProducToOrder(orderId,request);
        if(!response.getCode().equals("000")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order-status/{status}")
    public ResponseEntity<ApiResponseListOrdersByStatus> getOrdersByStatus(@PathVariable String status) {
        ApiResponseListOrdersByStatus response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/dispatch/{orderId}")
    public Mono<ResponseEntity<ChangeStatusResponse>> moveOrderDispatch(@PathVariable("orderId") Long orderId){
        return changeOrderService.moveToDispatchReactive(orderId)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest()
                        .body(new ChangeStatusResponse(orderId,null, ex.getMessage()))));
    }

    @PatchMapping("/delivered/{orderId}")
    public Mono<ResponseEntity<ChangeStatusResponse>> moveToDeliver(@PathVariable("orderId") Long orderId){
        return changeOrderService.moveDelivered(orderId)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest()
                        .body(new ChangeStatusResponse(orderId,null, ex.getMessage()))));
    }

    @GetMapping("/abandoned/count")
    public ResponseEntity<Map<String, Object>> getAbandonOrderCount(){
        long count = changeOrderService.getAbandonedOrderCount();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("abandonedOrdersCount", count);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/notabandoned/count")
    public ResponseEntity<Map<String, Object>> getOrdersCountNotAbandoned(){
        long count = changeOrderService.getOrdersCountNotAbandoned();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("ordersCountNotAbandoned", count);

        return ResponseEntity.ok(response);
    }
}
