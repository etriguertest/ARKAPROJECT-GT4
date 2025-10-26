package com.arka.movements.aws;
import com.arka.movements.dto.InventoryMovementRequestDto;
import com.arka.movements.entity.InventoryTransaction;
import com.arka.movements.service.InventoryMovementService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class MessageConsumer {
    InventoryMovementService inventoryMovementService;
    // Use Constructor Injection (Recommended)
    public MessageConsumer(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }
    // The method listens for messages on the specified queue name
    // The framework handles polling, message receipt, and automatic deletion on success.
    @SqsListener("queue-to-ec2-movements")
    public void listen(@Payload String message) {
        System.out.println("Received message: " + message);
        SqsTransformer sqsTransformer = new SqsTransformer();
        Flux<InventoryTransaction> stringToFlux=sqsTransformer.convertJsonStringToFlux(message);
        inventoryMovementService.insert(stringToFlux)// Add logging or error handling for visibility
                .doOnSuccess(v -> System.out.println("Inventory transactions successfully processed."))
                .doOnError(e -> System.err.println("Database error during insert: " + e.getMessage()))
                // 🚨 This triggers the data flow and executes repository.saveAll() 🚨
                .subscribe();
        stringToFlux// Use doOnNext for side effects like logging or printing
                .doOnNext(dto -> {
                    System.out.println("Processed by doOnNext: " + dto.getMovementType());
                })
                // You can chain other non-blocking operations here (e.g., map, filter)

                // The subscribe() call triggers the execution of the entire chain
                .subscribe();
        // Your business logic goes here
    }

    // For consuming complex JSON objects, you can use your custom class
    /*
    @SqsListener("my-object-queue")
    public void listen(MyObject payload) {
        System.out.println("Received object: " + payload.getData());
    }
    */
}