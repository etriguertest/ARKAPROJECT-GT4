package com.arka.customer.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
