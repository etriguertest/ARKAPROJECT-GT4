package com.arka.order.Dto;

import lombok.Data;

@Data
public class RequestCheckIfStock {
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public RequestCheckIfStock() {
    }
    public RequestCheckIfStock(Long productId) {
        this.productId = productId;
    }


}
