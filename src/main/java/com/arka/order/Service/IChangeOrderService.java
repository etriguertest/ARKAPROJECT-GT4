package com.arka.order.Service;

import com.arka.order.Dto.Response.ChangeStatusResponse;
import reactor.core.publisher.Mono;

public interface IChangeOrderService {
    Mono<ChangeStatusResponse> moveToDispatchReactive(Long orderId);
    Mono<ChangeStatusResponse> moveDelivered(Long orderId);
}
