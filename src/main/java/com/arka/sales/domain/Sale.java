package com.arka.sales.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Table("sales")
public class Sale {
    @Id
    private Long id;
    private Long orderId;
    private Long customerId;
    private String customerName;
    private BigDecimal total;
    private OffsetDateTime createdAt;
    private BigDecimal impuesto;
}
