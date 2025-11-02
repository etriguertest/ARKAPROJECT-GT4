package com.arka.sales.repository;

import com.arka.sales.domain.Sale;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SaleRepository extends R2dbcRepository<Sale, Long> {

}