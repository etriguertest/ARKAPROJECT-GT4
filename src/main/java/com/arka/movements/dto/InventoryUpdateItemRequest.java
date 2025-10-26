package com.arka.movements.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateItemRequest {
    private Long productId;
    private Integer type;
    private Integer quantity;
}