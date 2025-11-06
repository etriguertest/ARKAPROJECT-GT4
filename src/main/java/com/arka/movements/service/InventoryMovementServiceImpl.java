package com.arka.movements.service;

import com.arka.movements.dto.InventoryMovementRequestDto;
import com.arka.movements.dto.InventoryUpdateItemRequest;
import com.arka.movements.dto.enums.InventoryMovementType;
import com.arka.movements.dto.enums.StockChangeType;
import com.arka.movements.entity.Inventory;
import com.arka.movements.entity.InventoryTransaction;
import com.arka.movements.repository.InventoryMovementsRepository;
import lombok.RequiredArgsConstructor;
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

}
