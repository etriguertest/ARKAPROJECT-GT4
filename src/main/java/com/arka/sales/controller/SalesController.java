package com.arka.sales.controller;

import com.arka.sales.dto.SaleDto;
import com.arka.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping(value = "/confirm/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SaleDto> confirmOrder(@PathVariable Long orderId) {
        return salesService.confirmOrderAndCreateSale(orderId);
    }
}