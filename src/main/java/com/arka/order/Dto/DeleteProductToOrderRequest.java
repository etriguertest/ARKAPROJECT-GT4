package com.arka.order.Dto;

import java.util.List;

public class DeleteProductToOrderRequest {
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
