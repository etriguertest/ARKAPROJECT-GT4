package com.arka.order.Dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DeleteProductToOrderRequest {
    @NotNull(message = "Debe existir por lo menos un producto para eliminarse")
    private List<Long> productId;

    public DeleteProductToOrderRequest() {
    }

    public DeleteProductToOrderRequest(List<Long> productId) {
        this.productId = productId;
    }

    public List<Long> getProductId() {
        return productId;
    }

    public void setProductId(List<Long> productId) {
        this.productId = productId;
    }
}
