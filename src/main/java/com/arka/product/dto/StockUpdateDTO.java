package com.arka.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// Usamos @Data para getters/setters/etc.
@Data
@NoArgsConstructor
public class StockUpdateDTO {
    private Long id;
    private Integer stock;
}

