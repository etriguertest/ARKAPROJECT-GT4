package com.arka.inventory.dto.queue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryQueueItemStockProductMessage {
    private Long id;
    private Integer stock;
}
