package com.arka.order.Dto.ResponseErrors;

public class ErrorResponseDto {
    private String message;
    private String codResponse;
    private String description;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String message, String codResponse, String description) {
        this.message = message;
        this.codResponse = codResponse;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
