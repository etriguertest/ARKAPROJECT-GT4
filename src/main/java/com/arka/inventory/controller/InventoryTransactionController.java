package com.arka.inventory.controller;

import com.arka.inventory.dto.queue.InventoryQueueMovementMessage;
import com.arka.inventory.entity.InventoryTransaction;
import com.arka.inventory.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventory-movements")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryTransactionController {
    private final InventoryMovementService service;

    /**
     * POST /api/v1/inventory-movements
     * Inserts a new inventory transaction.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InventoryTransaction> insertTransaction(@RequestBody InventoryQueueMovementMessage transaction) {
        System.out.println("insertTransaction");
        return service.insert(transaction);
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

    @GetMapping("/ejemplo")
    public Mono<String> holaMundo() {

        WebClient client = WebClient.create("https://jsonplaceholder.typicode.com");

        // Llamada REST que devuelve un Mono<String>
        Mono<String> response = client.get()
                .uri("/posts/1") // recurso dummy
                .retrieve()
                .bodyToMono(String.class);

        // Suscripción al Mono
        response.subscribe(
                valor -> System.out.println("onNext: " + valor),   // cuando llega la respuesta
                error -> System.err.println("onError: " + error), // si ocurre error
                () -> System.out.println("onComplete: flujo terminado") // cuando finaliza

        );

        // ⚠️ Como es un flujo reactivo asíncrono, damos un pequeño sleep
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        return Mono.just("null");
    }

}

