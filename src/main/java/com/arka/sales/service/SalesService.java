package com.arka.sales.service;

import com.arka.sales.domain.Sale;
import com.arka.sales.domain.SaleItem;
import com.arka.sales.dto.OrderDto;
import com.arka.sales.dto.OrderItemDto;
import com.arka.sales.dto.SaleDto;
import com.arka.sales.dto.SaleItemDto;
import com.arka.sales.repository.SaleItemRepository;
import com.arka.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private static final Logger log = LoggerFactory.getLogger(SalesService.class);

    private final WebClient orderWebClient;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final TransactionalOperator txOperator;

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
        Sale sale = new Sale();
        sale.setOrderId(order.getIdOrder());
        sale.setTotal(order.getTotalAmount());
        sale.setCreatedAt(OffsetDateTime.now());

        return txOperator.execute(status ->
                saleRepository.save(sale)
                        .flatMap(savedSale ->
                                Flux.fromIterable(order.getItems())
                                        .map(this::toSaleItem)
                                        .flatMap(item -> {
                                            item.setSaleId(savedSale.getId());
                                            return saleItemRepository.save(item);
                                        })
                                        .collectList()
                                        .map(items -> toSaleDto(savedSale, items))
                        )
        ).single();
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
}
