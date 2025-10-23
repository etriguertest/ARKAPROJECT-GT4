package com.arka.product.repository;

import com.arka.product.dto.ProductDTO;
import com.arka.product.model.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
    @Query("""
        SELECT p.id , p.name, p.description, p.price, p.stock, c.nombre AS categoryName
        FROM arka.products AS p
        LEFT JOIN arka.category AS c ON p.category_id = c.id
    """)
    Flux<ProductDTO> findAllWithCategoryName();

    // --- (Opcional) Haz lo mismo para 'findById' ---
    @Query("""
        SELECT p.id, p.name, p.description, p.price, p.stock, c.nombre AS categoryName
        FROM arka.products AS p
        LEFT JOIN arka.category AS c ON p.category_id = c.id
        WHERE p.id = :id
    """)
    Mono<ProductDTO> findByIdWithCategoryName(Long id);
}
