package com.arka.sales.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {
    private Long idOrder;
    private Long customerId;
    private String status;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
}