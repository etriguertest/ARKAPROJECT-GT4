package com.arka.movements.controller;

import com.arka.movements.aws.MessageProducerService;
import com.arka.movements.dto.InventoryMovementRequestDto;
import com.arka.movements.entity.InventoryTransaction;
import com.arka.movements.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventory-movements")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryTransactionController {
    private final InventoryMovementService service;
    private final MessageProducerService messageProducerService;


    /**
     * POST /api/v1/inventory-movements
     * Inserts a new inventory transaction.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> insertTransaction(@RequestBody InventoryMovementRequestDto transaction) {
        System.out.println("insertTransaction");
        String jsonMovementList = """
            [
                {
                    "inventoryUnitId": 31, 
                    "movementType": "RECEPCION_POR_COMPRA",
                    "movementDate": "2025-10-18",
                    "documentReference": "PO-000551",
                    "idOperator": 101,
                    "nameOperator": "Alice M.",
                    "quantityType": "INCREASE",
                    "quantity": 10,
                    "fromBranch": 1,
                    "toBranch":1
                },
                {
                    "inventoryUnitId": 31, 
                    "movementType": "RECEPCION_POR_COMPRA",
                    "movementDate": "2025-10-18",
                    "documentReference": "PO-000551",
                    "idOperator": 101,
                    "nameOperator": "Alice M.",
                    "quantityType": "INCREASE",
                    "quantity": 10,
                    "fromBranch": 1,
                    "toBranch":1
                }
            ]
            """;

        messageProducerService.send(jsonMovementList);
        return Mono.just("Completado");
    }

    /**
     * GET /api/v1/inventory-movements/range
     * Finds transactions within a specified date range.
     * Example: GET /api/v1/inventory-movements/range?start=2023-10-01&end=2023-10-31
     */
    @GetMapping("/range")
    public Flux<InventoryTransaction> findByDateRange(
            @RequestParam("start") LocalDate startDate,
            @RequestParam("end") LocalDate endDate) {

        return service.findByMovementDateBetween(startDate, endDate);
    }

    @GetMapping("/last-movements/{inventoryUnitId}")
    public Flux<InventoryTransaction> findLastMovementsByUnit(
            @PathVariable("inventoryUnitId") Long inventoryUnitId) {
        return service.findLastTenMovementsByInventoryUnitId(inventoryUnitId);
    }

    @GetMapping("/search")
    public Flux<InventoryTransaction> findByFilters(
            @RequestParam(value = "inventoryUnitId", required = false) Long inventoryUnitId,
            @RequestParam(value = "movementType", required = false) String movementType,
            @RequestParam(value = "start", required = false) LocalDate startDate,
            @RequestParam(value = "end", required = false) LocalDate endDate,
            @RequestParam(value = "documentReference", required = false) String documentReference,
            @RequestParam(value = "fromBranch", required = false) Long fromBranch) {

        return service.findByFilters(
                inventoryUnitId,
                movementType,
                startDate,
                endDate,
                documentReference,
                fromBranch
        );
    }
}
