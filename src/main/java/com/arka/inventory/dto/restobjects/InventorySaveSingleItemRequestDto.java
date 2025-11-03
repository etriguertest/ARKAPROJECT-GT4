package com.arka.inventory.dto.restobjects;

import com.arka.inventory.dto.enums.StockChangeType;
import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventorySaveSingleItemRequestDto {
    private Long productId;
    private BigDecimal acquisitionCost;
    private Integer quantity;
    private Long branchId;

    // You will need this method in your DTO class:
    public Inventory toEntity() {
        Inventory inventory = new Inventory();
        inventory.setProductId(this.productId);
        inventory.setDateReceived(LocalDate.now());
        inventory.setManufactureDate(LocalDate.now().minusYears(2));
        inventory.setWarrantyEndDate(LocalDate.now().plusYears(1));
        if (this.quantity > 50) {
            inventory.setStatus(StockChangeType.IN_STOCK.getLabel());
        } else if (this.quantity > 5) {
            inventory.setStatus(StockChangeType.LOWSTOCK.getLabel());
        } else {
            inventory.setStatus(StockChangeType.OUTOFSTOCK.getLabel());
        }
        inventory.setAcquisitionCost(this.acquisitionCost);
        inventory.setQuantity(this.quantity);
        inventory.setBranchId(this.branchId);
        return inventory;
    }

}
