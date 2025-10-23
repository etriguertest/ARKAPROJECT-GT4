package com.arka.product.controller;

import com.arka.product.dto.ProductDTO;
import com.arka.product.model.Product;
import com.arka.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) { this.repo = repo; }

    @GetMapping
    public Flux<ProductDTO> all() {
        return repo.findAllWithCategoryName();
    }

    @GetMapping("/{id}")
    public Mono<ProductDTO> get(@PathVariable Long id) {
        return repo.findByIdWithCategoryName(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@RequestBody Product p) { return repo.save(p); }

    @PutMapping("/{id}")
    public Mono<Product> update(@PathVariable Long id, @RequestBody Product p) {
        return repo.findById(id)
                .flatMap(existing -> {
                    existing.setName(p.getName());
                    existing.setDescription(p.getDescription());
                    existing.setPrice(p.getPrice());
                    existing.setStock(p.getStock());
                    existing.setSku(p.getSku());
                    existing.setCategoryId(p.getCategoryId());
                    return repo.save(existing);
                });
    }
}
