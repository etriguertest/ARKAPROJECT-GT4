package com.arka.order.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductStockValidationResponse {
    private String message;
    private String codResponse;
    private List<ResponseCheckIfStock> foundInventories;
    private List<ResponseCheckIfStock> notFoundProductStock;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodResponse() {
        return codResponse;
    }

    public void setCodResponse(String codResponse) {
        this.codResponse = codResponse;
    }

    public List<ResponseCheckIfStock> getFoundInventories() {
        return foundInventories;
    }

    public void setFoundInventories(List<ResponseCheckIfStock> foundInventories) {
        this.foundInventories = foundInventories;
    }

    public List<ResponseCheckIfStock> getNotFoundProductStock() {
        return notFoundProductStock;
    }

    public void setNotFoundProductStock(List<ResponseCheckIfStock> notFoundProductStock) {
        this.notFoundProductStock = notFoundProductStock;
    }
}
