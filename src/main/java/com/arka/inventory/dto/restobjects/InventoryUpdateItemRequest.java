package com.arka.inventory.dto.restobjects;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateItemRequest {
    private Long numOrder;
    private Long productId;
    private Integer type;
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer quantity;

//    private String documentId;

}