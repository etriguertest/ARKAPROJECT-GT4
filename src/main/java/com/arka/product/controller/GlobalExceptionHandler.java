package com.arka.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Este método captura las excepciones de validación (@Valid) en proyectos
     * reactivos (WebFlux) como el tuyo.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Sigue devolviendo un 400
    public Map<String, Object> handleValidationExceptions(WebExchangeBindException ex) {

        // Creamos un mapa para guardar los errores de campo
        Map<String, String> fieldErrors = new HashMap<>();

        // Iteramos sobre todos los errores de campo que encontró la validación
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Añadimos al mapa: el nombre del campo y el mensaje de error
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        // Devolvemos un mapa con un formato más claro
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("fieldErrors", fieldErrors); // Aquí están tus mensajes

        return response;
    }
}
