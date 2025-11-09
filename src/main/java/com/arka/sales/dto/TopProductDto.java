package com.arka.sales.dto;

import lombok.Data;

@Data
public class TopProductDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
}
