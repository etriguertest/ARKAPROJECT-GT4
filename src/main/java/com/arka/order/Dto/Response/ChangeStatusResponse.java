package com.arka.order.Dto.Response;

public record ChangeStatusResponse(
        Long orderId,
        String status,
        String message
) { }
