package com.arka.sales.dto;

import lombok.Data;

@Data
public class TopCustomerDto {
    private Long customerId;
    private String customerName;
    private Long totalPurchases;
}
