package com.arka.inventory.dto.enums;

public enum StockChangeType {

    DECREASE("DECREMENT"),
    INCREASE("INCREMENT"),
    OUTOFSTOCK("OUTOFSTOCK"),
    LOWSTOCK("LOWSTOCK"),
    IN_STOCK("IN STOCK");


    private final String label;

    StockChangeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
