package com.arka.customer.controller;

import com.arka.customer.dto.AuthRequest;
import com.arka.customer.dto.AuthResponse;
import com.arka.customer.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            String token = authService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }


    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String error;
    }
}
