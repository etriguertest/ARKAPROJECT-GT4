package com.arka.product.sqs;

import com.arka.product.dto.StockUpdateDTO;
import com.arka.product.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; // Importar
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ProductStockListener {

    private static final Logger log = LoggerFactory.getLogger(ProductStockListener.class);
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper; // <-- 1. Agrega ObjectMapper

    // 2. Inyecta ObjectMapper en el constructor
    public ProductStockListener(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Escucha la cola. Maneja tanto payloads de String (text/plain)
     * como payloads de Lista (application/json)
     */
    @SqsListener("queue-to-product")
    public void handleStockUpdate(@Payload Object payload) { // <-- 3. Acepta Object

        List<StockUpdateDTO> updateList;

        // 4. Lógica de validación de tipo
        if (payload instanceof String payloadString) {
            // CASO A: El payload es un String (contentType=text/plain)
            log.info("Payload recibido como String, parseando manualmente...");
            try {
                updateList = objectMapper.readValue(payloadString,
                        new TypeReference<List<StockUpdateDTO>>() {});
            } catch (Exception e) {
                log.error("Error FATAL al parsear JSON String de SQS: {}", payloadString, e);
                return; // Abortar procesamiento
            }

        } else if (payload instanceof List) {
            // CASO B: El payload ya es una Lista (contentType=application/json)
            // El emisor fue arreglado. Spring lo deserializó automáticamente.
            log.info("Payload recibido como Lista, convirtiendo a DTOs...");
            try {
                // Usamos convertValue para asegurar que sea List<StockUpdateDTO>
                // y no List<LinkedHashMap> (un problema común)
                updateList = objectMapper.convertValue(payload,
                        new TypeReference<List<StockUpdateDTO>>() {});
            } catch (Exception e) {
                log.error("Error al convertir payload de Lista a DTOs: {}", payload, e);
                return; // Abortar procesamiento
            }
        } else {
            // CASO C: Tipo de payload inesperado
            log.error("Tipo de payload inesperado recibido: {}", payload.getClass().getName());
            return; // Abortar procesamiento
        }
        

        if (updateList.isEmpty()) {
            log.warn("El payload resultó en una lista vacía. Nada que procesar.");
            return;
        }

        log.info("Parseo exitoso: Actualizar stock para {} productos",
                updateList.size());

        // Convertimos la lista en un Flux (un stream reactivo)
        Flux.fromIterable(updateList)
                .flatMap(updateDto -> { // Procesamos CADA DTO de la lista

                    log.debug("Procesando ID {} a stock {}", updateDto.getId(), updateDto.getStock());

                    // Esta es tu lógica original
                    return productRepository.findById(updateDto.getId())
                            .flatMap(product -> {
                                product.setStock(updateDto.getStock());
                                return productRepository.save(product);
                            })
                            .doOnSuccess(savedProduct -> {
                                log.info("Stock actualizado exitosamente para: {}", savedProduct.getName());
                            })
                            .switchIfEmpty(Mono.defer(() -> {
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
                .subscribe(); // Inicia la cadena reactiva
    }
}