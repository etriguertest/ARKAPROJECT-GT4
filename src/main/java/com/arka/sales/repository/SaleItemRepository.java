package com.arka.sales.repository;

import com.arka.sales.domain.SaleItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SaleItemRepository extends R2dbcRepository<SaleItem, Long> {

}