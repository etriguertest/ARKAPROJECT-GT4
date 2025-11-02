package com.arka.sales.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class SaleDto {
    private Long id;
    private Long orderId;
    private BigDecimal total;
    private OffsetDateTime createdAt;
    private List<SaleItemDto> items;
}