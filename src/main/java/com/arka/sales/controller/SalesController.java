package com.arka.sales.controller;

import com.arka.sales.domain.Sale;
import com.arka.sales.dto.SaleDto;
import com.arka.sales.dto.SaleItemDto;
import com.arka.sales.dto.SalesSummaryDto;
import com.arka.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin
public class SalesController {

    private final SalesService salesService;

    @PostMapping(value = "/confirm/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SaleDto> confirmOrder(@PathVariable Long orderId) {
        return salesService.confirmOrderAndCreateSale(orderId);
    }


    @GetMapping()
    public Flux<Sale> getSales(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  toDate
    ) {
        return salesService.findSalesByFilters(orderId, customerId, customerName, fromDate, toDate);
    }


    @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SaleItemDto>> getSaleDetailById(@PathVariable("id") Long id) {
        return salesService.getSaleDetailById(id);
    }


    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SalesSummaryDto> getSalesSummary() {
        return salesService.getSalesSummary();
    }
}
