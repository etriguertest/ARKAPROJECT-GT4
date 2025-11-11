package com.arka.order.Service;

import com.arka.order.Dto.Response.ChangeStatusResponse;
import com.arka.order.Entity.Order;
import reactor.core.publisher.Mono;

public interface IChangeOrderService {
    Mono<ChangeStatusResponse> moveToDispatchReactive(Long orderId);
    Mono<ChangeStatusResponse> moveDelivered(Long orderId);
    void sendEmailToConfirm(Order order);
    long getAbandonedOrderCount();
    long getOrdersCountNotAbandoned();
}
