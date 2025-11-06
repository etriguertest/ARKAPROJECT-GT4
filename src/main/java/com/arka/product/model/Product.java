package com.arka.product.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {
    @Id
    private Long id;
    @NotNull(message = "El nombre del producto es obligatorio")
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;
    private String description;
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private BigDecimal price;
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    @Column("category_id")
    private Integer categoryId;
}
