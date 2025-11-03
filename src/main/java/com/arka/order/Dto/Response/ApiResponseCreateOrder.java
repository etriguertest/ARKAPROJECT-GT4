package com.arka.order.Dto.Response;

import lombok.Builder;

@Builder
public class ApiResponseCreateOrder<T> {
    private String code;
    private String message;
    private T body;

    public ApiResponseCreateOrder() {
    }

    public ApiResponseCreateOrder(String code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    // Builder manual
    public static class Builder<T> {
        private String code;
        private String message;
        private T body;

        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> body(T body) {
            this.body = body;
            return this;
        }

        public ApiResponseCreateOrder<T> build() {
            return new ApiResponseCreateOrder<>(code, message, body);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
