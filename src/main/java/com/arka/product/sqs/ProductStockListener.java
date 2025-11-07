package com.arka.product.sqs;

import com.arka.product.dto.StockUpdateDTO;
import com.arka.product.repository.ProductRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux; // Importante: Usar Flux
import reactor.core.publisher.Mono;

import java.util.List; // Importante: Usar List

@Component
public class ProductStockListener {

    private static final Logger log = LoggerFactory.getLogger(ProductStockListener.class);
    private final ProductRepository productRepository;

    public ProductStockListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Escucha la cola, parsea el JSON automáticamente a una LISTA de StockUpdateDto
     * y actualiza cada producto.
     */
    @SqsListener("queue-to-product")
    public void handleStockUpdate(@Payload List<StockUpdateDTO> updateList) { // <-- CAMBIO AQUÍ

        log.info("Mensaje SQS (batch) recibido: Actualizar stock para {} productos",
                updateList.size());

        // Convertimos la lista en un Flux (un stream reactivo)
        Flux.fromIterable(updateList)
                .flatMap(updateDto -> { // Procesamos CADA DTO de la lista

                    log.debug("Procesando ID {} a stock {}", updateDto.getId(), updateDto.getStock());

                    // Esta es tu lógica original, ahora aplicada a cada elemento del Flux
                    return productRepository.findById(updateDto.getId())
                            .flatMap(product -> {
                                // Producto encontrado, actualizamos el stock
                                product.setStock(updateDto.getStock());
                                // Guardamos el producto actualizado
                                return productRepository.save(product);
                            })
                            .doOnSuccess(savedProduct -> {
                                log.info("Stock actualizado exitosamente para: {}", savedProduct.getName());
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // Esto se ejecuta si findById no encontró nada
                                log.warn("Producto no encontrado con ID: {}. Mensaje SQS ignorado.",
                                        updateDto.getId());
                                return Mono.empty();
                            }));
                })
                .doOnError(e -> {
                    log.error("Error al procesar el batch de actualización de stock", e);
                })
                .doOnComplete(() -> {
                    log.info("Batch de SQS procesado completamente.");
                })
                .subscribe(); // IMPORTANTE: Inicia la cadena reactiva para TODO el batch
    }
}