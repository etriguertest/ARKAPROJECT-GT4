package com.arka.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookRequestDto {
    String emailCustomer;
    String userName;
    String orderId;
    String statusOrder;

}


//    private String triggerEvent;
//    private String payloadData;
//
//    // Constructors, Getters, and Setters
//    public WebhookRequestDto(String triggerEvent, String payloadData) {
//        this.triggerEvent = triggerEvent;
//        this.payloadData = payloadData;
//    }
