package com.arka.product.sqs;

import com.arka.product.dto.StockUpdateDTO;
import com.arka.product.repository.ProductRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductStockListener {

    private static final Logger log = LoggerFactory.getLogger(ProductStockListener.class);
    private final ProductRepository productRepository;

    // Inyectamos el ProductRepository que ya tienes
    public ProductStockListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Escucha la cola, parsea el JSON automáticamente a un StockUpdateDto
     * y actualiza el producto.
     */
    @SqsListener("queue-to-product")
    public void handleStockUpdate(@Payload StockUpdateDTO updateDto) {

        log.info("Mensaje SQS recibido: Actualizar stock para ID {} a {}",
                updateDto.getId(), updateDto.getStock());

        // Esta es la lógica reactiva para actualizar tu producto
        productRepository.findById(updateDto.getId())
                .flatMap(product -> {
                    // Producto encontrado, actualizamos el stock
                    product.setStock(updateDto.getStock());
                    // Guardamos el producto actualizado
                    return productRepository.save(product);
                })
                .doOnSuccess(savedProduct -> {
                    log.info("Stock actualizado exitosamente para: {}", savedProduct.getName());
                })
                .doOnError(e -> {
                    log.error("Error al procesar la actualización de stock", e);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // Esto se ejecuta si findById no encontró nada
                    log.warn("Producto no encontrado con ID: {}. Mensaje SQS ignorado.",
                            updateDto.getId());
                    return Mono.empty();
                }))
                .subscribe(); // IMPORTANTE: Inicia la cadena reactiva
    }
}
