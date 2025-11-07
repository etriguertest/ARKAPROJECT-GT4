package com.arka.sales.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Data
@Table("sale_items")
public class SaleItem {
    @Id
    private Long id;
    private Long saleId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
