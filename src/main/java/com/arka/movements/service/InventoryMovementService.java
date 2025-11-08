package com.arka.movements.service;

import com.arka.movements.dto.InventoryMovementRequestDto;
import com.arka.movements.dto.InventoryUpdateItemRequest;
import com.arka.movements.entity.Inventory;
import com.arka.movements.entity.InventoryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface InventoryMovementService {
    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<InventoryTransaction> insert(InventoryMovementRequestDto transaction);

    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<Void> insert(Flux<InventoryTransaction> transaction);
    /**
     * Finds all inventory transactions that occurred within the specified date range.
     * @param startDate The start date (inclusive).
     * @param endDate The end date (inclusive).
     * @return A Flux emitting the matching transactions.
     */
    Flux<InventoryTransaction> findByMovementDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<Boolean> saveList(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests, List<Inventory> transaction);
    /**
     * Finds the last N inventory transactions for a specific inventory unit.
     * @param inventoryUnitId The ID of the inventory unit (product).
     * @return A Flux emitting the last 10 matching transactions.
     */
    Flux<InventoryTransaction> findLastTenMovementsByInventoryUnitId(Long inventoryUnitId);

    Flux<InventoryTransaction> findByFilters(
            Long inventoryUnitId,
            String movementType,
            LocalDate startDate,
            LocalDate endDate,
            String documentReference,
            Long fromBranch);
}
