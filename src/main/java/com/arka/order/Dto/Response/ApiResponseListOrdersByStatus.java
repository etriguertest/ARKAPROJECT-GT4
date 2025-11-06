package com.arka.order.Dto.Response;

import com.arka.order.Dto.OrderResponse;

import java.util.List;

public class ApiResponseListOrdersByStatus {
    private boolean success;
    private String message;
    private List<OrderResponse> orders;

    private ApiResponseListOrdersByStatus(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.orders = builder.orders;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public static class Builder {
        private boolean success;
        private String message;
        private List<OrderResponse> orders;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder orders(List<OrderResponse> orders) {
            this.orders = orders;
            return this;
        }

        public ApiResponseListOrdersByStatus build() {
            return new ApiResponseListOrdersByStatus(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
