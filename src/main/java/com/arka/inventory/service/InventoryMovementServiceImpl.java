package com.arka.inventory.service;

import com.arka.inventory.aws.MessageProducerService;
import com.arka.inventory.aws.SnsPublisherService;
import com.arka.inventory.aws.SqsTransformer;
import com.arka.inventory.dto.enums.InventoryMovementType;
import com.arka.inventory.dto.enums.StockChangeType;
import com.arka.inventory.dto.queue.InventoryQueueItemStockProductMessage;
import com.arka.inventory.dto.queue.InventoryQueueMovementMessage;
import com.arka.inventory.dto.restobjects.InventoryUpdateItemRequest;
import com.arka.inventory.entity.Inventory;
import com.arka.inventory.entity.InventoryTransaction;
import com.arka.inventory.repository.InventoryMovementsRepository;
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
    private final MessageProducerService messageProducerService;
    private final SnsPublisherService snsPublisherService;

    @Override
    public Mono<InventoryTransaction> insert(InventoryQueueMovementMessage transaction) {

        System.out.println("insert");
        // Here you would add validation/business logic before saving.
        return repository.save(transaction.toEntity());
    }

    @Override
    public Flux<InventoryTransaction> findByMovementDateBetween(LocalDate startDate, LocalDate endDate) {
        return repository.findByMovementDateBetween(startDate, endDate);
    }


    @Override
    public Mono<Boolean> saveList(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests,List<Inventory> transaction) {
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
    public Mono<Boolean> senListToAWSQueue(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests, List<Inventory> transaction) {
        System.out.println("Guardando inventoryTransactionList");
        List<InventoryTransaction> inventoryTransactionList = new ArrayList<>();
        int productType=inventoryUpdateItemRequests.get(0).getType();
        List<InventoryQueueItemStockProductMessage> lstInventoryQueueToProduc = new ArrayList<>();
        for (Inventory item : transaction){
            InventoryTransaction movement = new InventoryTransaction();
            movement.setInventoryUnitId(item.getId());
            if(productType==0){
                movement.setMovementType(InventoryMovementType.VENTA_DESPACHO_CLIENTE.getDescripcion());
                movement.setQuantityType(StockChangeType.DECREASE.getLabel());
            }else if(productType==1){
                movement.setMovementType(InventoryMovementType.RECEPCION_POR_COMPRA.getDescripcion());
                movement.setQuantityType(StockChangeType.INCREASE.getLabel());

            }else{
                movement.setMovementType(InventoryMovementType.AJUSTE_INVENTARIO.getDescripcion());
                movement.setQuantityType(StockChangeType.ADJUST.getLabel());

            }
            movement.setMovementDate(LocalDate.now());
            movement.setIdOperator(Long.valueOf("1"));
            movement.setNameOperator("Juan Perez");
            movement.setFromBranch(item.getBranchId());
//            int quantity = inventoryUpdateItemRequests.stream()
//                    .filter(request -> request.getProductId().equals(item.getProductId())) // Filter by matching ID
//                    .findFirst() // Get the first match
//                    .map(updateItemRequest -> updateItemRequest.getQuantity()) // Extract the quantity
//                    .orElse(0); // Default to 0 if not found
            InventoryUpdateItemRequest matchingRequest = inventoryUpdateItemRequests.stream()
                    .filter(request -> request.getProductId().equals(item.getProductId())) // Filter by matching ID
                    .findFirst() // Returns Optional<InventoryUpdateItemRequest>
                    .orElse(new InventoryUpdateItemRequest());

            movement.setQuantity(matchingRequest.getQuantity());
            System.out.println("quzntiyus"+matchingRequest.getQuantity());
            inventoryTransactionList.add(movement);
            movement.setDocumentReference(matchingRequest.getNumOrder() != null
                    ? matchingRequest.getNumOrder().toString()
                    : "");


            InventoryQueueItemStockProductMessage inventoryQueueItemStockProductMessage = new InventoryQueueItemStockProductMessage();
            inventoryQueueItemStockProductMessage.setId(item.getProductId());
            inventoryQueueItemStockProductMessage.setStock(item.getQuantity());
            lstInventoryQueueToProduc.add(inventoryQueueItemStockProductMessage);
        }

        SqsTransformer sqsTransformer = new SqsTransformer();
        Mono<String> monoString = sqsTransformer.listToString(inventoryTransactionList);
        String result = monoString.block();
        Mono<String> monoStringQueue = sqsTransformer.listQueueToProductToString(lstInventoryQueueToProduc);
        String resultQueue = monoStringQueue.block();
//        System.out.println("result");
//        System.out.println("resultQueue");
//        messageProducerService.send(result);
        messageProducerService.sendToProducts(resultQueue);
        snsPublisherService.publishNotification("MOVIMIENTO-INVENTARIO",result);
        return Mono.just(true);

    }

}
