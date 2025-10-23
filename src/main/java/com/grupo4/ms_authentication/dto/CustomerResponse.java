package com.grupo4.ms_authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
}
