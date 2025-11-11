package com.arka.order.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AddProductToOrderRequest {

    private Long orderId;
    @Valid
    private List<OrderItemRequest> items;

    public AddProductToOrderRequest() {
    }

    public AddProductToOrderRequest(Long orderId, List<OrderItemRequest> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
