package com.grupo4.ms_authentication.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
