package com.arka.movements.service;

import com.arka.movements.dto.InventoryMovementRequestDto;
import com.arka.movements.dto.InventoryUpdateItemRequest;
import com.arka.movements.dto.enums.InventoryMovementType;
import com.arka.movements.dto.enums.StockChangeType;
import com.arka.movements.entity.Inventory;
import com.arka.movements.entity.InventoryTransaction;
import com.arka.movements.repository.InventoryMovementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryMovementServiceImpl implements InventoryMovementService{
    private final InventoryMovementsRepository repository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<InventoryTransaction> insert(InventoryMovementRequestDto transaction) {

        System.out.println("insert");
        // Here you would add validation/business logic before saving.
        return repository.save(transaction.toEntity());
    }

    @Override
    public Mono<Void> insert(Flux<InventoryTransaction> transaction) {
        return repository.saveAll(transaction).then();
    }

    @Override
    public Flux<InventoryTransaction> findByMovementDateBetween(LocalDate startDate, LocalDate endDate) {
        return repository.findByMovementDateBetweenOrderByMovementDateDesc(startDate, endDate);
    }
    @Override
    public Flux<InventoryTransaction> findLastTenMovementsByInventoryUnitId(Long inventoryUnitId) {
        return repository.findTop10ByInventoryUnitIdOrderByMovementDateDescIdDesc(inventoryUnitId);
    }

    @Override
    public Mono<Boolean> saveList(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests, List<Inventory> transaction) {
        System.out.println("Guardando inventoryTransactionList");
        List<InventoryTransaction> inventoryTransactionList = new ArrayList<>();
        int productType=inventoryUpdateItemRequests.get(0).getType();
        for (Inventory item : transaction){
            InventoryTransaction movement = new InventoryTransaction();
            movement.setInventoryUnitId(item.getId());
            if(productType==0){
                movement.setMovementType(InventoryMovementType.VENTA_DESPACHO_CLIENTE.getDescripcion());
                movement.setQuantityType(StockChangeType.DECREASE.getLabel());
            }else {
                movement.setMovementType(InventoryMovementType.RECEPCION_POR_COMPRA.getDescripcion());
                movement.setQuantityType(StockChangeType.INCREASE.getLabel());

            }
            movement.setMovementDate(LocalDate.now());
            movement.setDocumentReference("PEDIDO#XX");
            movement.setIdOperator(Long.valueOf("1"));
            movement.setNameOperator("Juan Perez");
            movement.setFromBranch(item.getBranchId());
            int quantity = inventoryUpdateItemRequests.stream()
                    .filter(request -> request.getProductId().equals(item.getProductId())) // Filter by matching ID
                    .findFirst() // Get the first match
                    .map(updateItemRequest -> updateItemRequest.getQuantity()) // Extract the quantity
                    .orElse(0); // Default to 0 if not found

            movement.setQuantity(quantity);
            System.out.println("quzntiyus"+quantity);
            inventoryTransactionList.add(movement);
        }
//        Flux.fromIterable(transaction);
        System.out.println("beforesave"+inventoryTransactionList);
        Flux<InventoryTransaction> dataSave=repository.saveAll(Flux.fromIterable(inventoryTransactionList));
        dataSave.subscribe();
        System.out.println("afteresave");
//        InventoryMovementRequestDto;
        return Mono.just(true);
    }


    @Override
    public Flux<InventoryTransaction> findByFilters(
            Long inventoryUnitId,
            String movementType,
            LocalDate startDate,
            LocalDate endDate,
            String documentReference,
            Long fromBranch) {

        // 1. Build the dynamic SQL query
        StringBuilder sql = new StringBuilder("SELECT * FROM inventory.inventory_transaction WHERE 1=1 ");

        if (inventoryUnitId != null) {
            sql.append("AND inventory_unit_id = :inventoryUnitId ");
        }
        if (movementType != null && !movementType.isEmpty()) {
            // Assuming movementType is an exact match or you might use LIKE
            sql.append("AND movement_type = :movementType ");
        }
        if (documentReference != null && !documentReference.isEmpty()) {
            // Using LIKE for documentReference for flexible searching (e.g., partial match)
            sql.append("AND LOWER(document_reference) LIKE LOWER(:documentReference) ");
        }
        if (fromBranch != null) {
            sql.append("AND from_branch = :fromBranch ");
        }
        // For date range, assuming a 'transaction_date' column
        if (startDate != null) {
            sql.append("AND movement_date >= :startDate ");
        }
        if (endDate != null) {
            sql.append("AND movement_date <= :endDate ");
        }

        sql.append("ORDER BY movement_date DESC, id DESC");

        // 2. Prepare the execution specification
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString());

        // 3. Bind the parameters dynamically
        if (inventoryUnitId != null) {
            spec = spec.bind("inventoryUnitId", inventoryUnitId);
        }
        if (movementType != null && !movementType.isEmpty()) {
            spec = spec.bind("movementType", movementType);
        }
        if (documentReference != null && !documentReference.isEmpty()) {
            spec = spec.bind("documentReference", "%" + documentReference + "%");
        }
        if (fromBranch != null) {
            spec = spec.bind("fromBranch", fromBranch);
        }
        if (startDate != null) {
            spec = spec.bind("startDate", startDate);
        }
        if (endDate != null) {
            spec = spec.bind("endDate", endDate);
        }

        // 4. Map the results and execute
        return spec.map((row, meta) -> {
                    InventoryTransaction transaction = new InventoryTransaction();
                    // Map columns to InventoryTransaction fields
                    transaction.setId(row.get("id", Long.class));
                    transaction.setInventoryUnitId(row.get("inventory_unit_id", Long.class));
                    transaction.setMovementType(row.get("movement_type", String.class));
                    transaction.setMovementDate(row.get("movement_date", LocalDate.class));
                    transaction.setQuantity(row.get("quantity", Integer.class)); // Example field
                    transaction.setDocumentReference(row.get("document_reference", String.class));
                    transaction.setFromBranch(row.get("from_branch", Long.class));
                    transaction.setQuantityType(row.get("quantity_type", String.class));
                    return transaction;
                })
                .all();
    }

}
