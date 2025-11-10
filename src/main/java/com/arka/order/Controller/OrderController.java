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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public ResponseEntity<ApiResponseCreateOrder<OrderResponse>> createOrder(@RequestBody CreateOrderRequest request){
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
            @PathVariable Long orderId, @RequestBody AddProductToOrderRequest request
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
            @RequestBody DeleteProductToOrderRequest request
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
}
