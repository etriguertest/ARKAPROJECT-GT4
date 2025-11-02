package com.arka.sales.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
