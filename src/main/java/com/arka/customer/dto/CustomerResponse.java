package com.arka.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
}
