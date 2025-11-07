package com.arka.sales.service;

import com.arka.sales.domain.Sale;
import com.arka.sales.domain.SaleItem;
import com.arka.sales.dto.*;
import com.arka.sales.repository.SaleItemRepository;
import com.arka.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private static final Logger log = LoggerFactory.getLogger(SalesService.class);

    private final WebClient orderWebClient;
    private final WebClient productWebClient;
    private final WebClient customerWebClient;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final TransactionalOperator txOperator;
    private final DatabaseClient databaseClient;

    private final Duration externalTimeout = Duration.ofSeconds(5);

    public Mono<SaleDto> confirmOrderAndCreateSale(Long orderId) {
        return orderWebClient.put()
                .uri("/order/confirm/{id}", orderId)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .timeout(externalTimeout)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .flatMap(order -> {
                    if (!"CONFIRMADO".equalsIgnoreCase(order.getStatus())) {
                        return Mono.error(new IllegalStateException("La orden no fue confirmada correctamente."));
                    }

                    return createAndReturnSavedSale(order);
                })
                .doOnError(ex -> log.error("Error confirmando orden {}: {}", orderId, ex.getMessage()));
    }


    private Mono<SaleDto> createAndReturnSavedSale(OrderDto order) {
        return fetchCustomer(order.getCustomerId())
                .flatMap(customer -> {
                    Sale sale = new Sale();
                    sale.setOrderId(order.getIdOrder());
                    sale.setCustomerId(order.getCustomerId());
                    sale.setCustomerName(customer.getName());
                    sale.setTotal(order.getTotalAmount());
                    sale.setCreatedAt(OffsetDateTime.now());
                    sale.setImpuesto(order.getTotalAmount().multiply(new BigDecimal("0.13")));

                    return txOperator.execute(status ->
                            saleRepository.save(sale)
                                    .flatMap(savedSale ->
                                            Flux.fromIterable(order.getItems())
                                                    .flatMap(itemDto -> fetchProductAndSaveItem(itemDto, savedSale.getId()))
                                                    .collectList()
                                                    .map(items -> toSaleDto(savedSale, items))
                                    )
                    ).single();
                });
    }



    private Mono<CustomerDto> fetchCustomer(Long customerId) {
        return customerWebClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .timeout(externalTimeout)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .doOnError(ex -> log.error("Error consultando cliente {}: {}", customerId, ex.getMessage()));
    }



    private Mono<SaleItem> fetchProductAndSaveItem(OrderItemDto itemDto, Long saleId) {
        return productWebClient.get()
                .uri("/products/{id}", itemDto.getProductId())
                .retrieve()
                .bodyToMono(ProductDto.class)
                .timeout(externalTimeout)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .flatMap(product -> {
                    SaleItem saleItem = new SaleItem();
                    saleItem.setSaleId(saleId);
                    saleItem.setProductId(itemDto.getProductId());
                    saleItem.setQuantity(itemDto.getQuantity());
                    saleItem.setPrice(itemDto.getPrice());
                    saleItem.setProductName(product.getName());
                    return saleItemRepository.save(saleItem);
                })
                .doOnError(ex -> log.error("Error consultando producto {}: {}", itemDto.getProductId(), ex.getMessage()));
    }



    private SaleItem toSaleItem(OrderItemDto oi) {
        SaleItem si = new SaleItem();
        si.setProductId(oi.getProductId());
        si.setQuantity(oi.getQuantity());
        si.setPrice(oi.getPrice());
        return si;
    }



    private SaleDto toSaleDto(Sale sale, List<SaleItem> items) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setOrderId(sale.getOrderId());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setTotal(sale.getTotal());
        dto.setItems(items.stream().map(si -> {
            SaleItemDto sid = new SaleItemDto();
            sid.setProductId(si.getProductId());
            sid.setQuantity(si.getQuantity());
            sid.setPrice(si.getPrice());
            return sid;
        }).collect(Collectors.toList()));
        return dto;
    }


    public Flux<Sale> findSalesByFilters(Long orderId,
                                         Long customerId,
                                         String customerName,
                                         OffsetDateTime fromDate,
                                         OffsetDateTime toDate) {

        StringBuilder sql = new StringBuilder("SELECT * FROM sales WHERE 1=1 ");

        if (orderId != null) {
            sql.append("AND order_id = :orderId ");
        }
        if (customerId != null) {
            sql.append("AND customer_id = :customerId ");
        }
        if (customerName != null && !customerName.isEmpty()) {
            sql.append("AND LOWER(customer_name) LIKE LOWER(:customerName) ");
        }
        if (fromDate != null) {
            sql.append("AND created_at >= :fromDate ");
        }
        if (toDate != null) {
            sql.append("AND created_at <= :toDate ");
        }

        sql.append("ORDER BY created_at DESC");

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString());

        if (orderId != null) {
            spec = spec.bind("orderId", orderId);
        }
        if (customerId != null) {
            spec = spec.bind("customerId", customerId);
        }
        if (customerName != null && !customerName.isEmpty()) {
            spec = spec.bind("customerName", "%" + customerName + "%");
        }
        if (fromDate != null) {
            spec = spec.bind("fromDate", fromDate);
        }
        if (toDate != null) {
            spec = spec.bind("toDate", toDate);
        }

        return spec.map((row, meta) -> {
                    Sale s = new Sale();
                    s.setId(row.get("id", Long.class));
                    s.setOrderId(row.get("order_id", Long.class));
                    s.setCustomerId(row.get("customer_id", Long.class));
                    s.setCustomerName(row.get("customer_name", String.class));
                    s.setTotal(row.get("total", BigDecimal.class));
                    s.setCreatedAt(row.get("created_at", OffsetDateTime.class));
                    s.setImpuesto(row.get("impuesto", BigDecimal.class));
                    return s;
                })
                .all();
    }


    public Mono<List<SaleItemDto>> getSaleDetailById(Long saleId) {
        return saleRepository.findById(saleId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Venta no encontrada con ID: " + saleId)))
                .flatMap(sale ->
                        saleItemRepository.findBySaleId(sale.getId())
                                .map(item -> {
                                    SaleItemDto dto = new SaleItemDto();
                                    dto.setProductId(item.getProductId());
                                    dto.setQuantity(item.getQuantity());
                                    dto.setPrice(item.getPrice());
                                    dto.setProductName(item.getProductName());
                                    return dto;
                                })
                                .collectList()
                )
                .doOnError(ex -> log.error("Error obteniendo detalle de venta {}: {}", saleId, ex.getMessage()));
    }
}
