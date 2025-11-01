package com.arka.order.Dto;

import lombok.Data;

@Data
public class ResponseCheckIfStock {
    private Long inventoryId;
    private Long productId;
    private Integer quantity;
    private Boolean isStockAvailable;

    public ResponseCheckIfStock(Long inventoryId, Long productId, Integer quantity, Boolean isStockAvailable) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.quantity = quantity;
        this.isStockAvailable = isStockAvailable;
    }

    public ResponseCheckIfStock() {
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getStockAvailable() {
        return isStockAvailable;
    }

    public void setStockAvailable(Boolean stockAvailable) {
        isStockAvailable = stockAvailable;
    }
}
