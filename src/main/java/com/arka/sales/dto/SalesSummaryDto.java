package com.arka.sales.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesSummaryDto {
    private BigDecimal totalSales;
    private List<TopProductDto> topProducts;
    private List<TopCustomerDto> topCustomers;
}
