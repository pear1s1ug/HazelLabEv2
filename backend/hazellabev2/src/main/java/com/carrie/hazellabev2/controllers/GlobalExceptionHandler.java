package com.carrie.hazellabev2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/* ================= Manejador Global de Excepciones ================= */

/**
 * Manejador centralizado de excepciones para toda la aplicación.
 * Intercepta y procesa excepciones no capturadas en controladores individuales.
 * Proporciona respuestas HTTP consistentes para diferentes tipos de errores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /* ================= MANEJO DE EXCEPCIONES DE RUNTIME ================= */
    
    /**
     * Maneja todas las excepciones RuntimeException no capturadas en la aplicación.
     * RuntimeException incluye la mayoría de excepciones no verificadas como:
     * - IllegalArgumentException (validaciones de negocio)
     * - IllegalStateException (estados inválidos)
     * - NullPointerException (accesos a nulos)
     * - Excepciones personalizadas de servicios
     * 
     * @param ex La excepción RuntimeException que fue lanzada
     * @return ResponseEntity con código HTTP 500 (Internal Server Error) y mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Retorna respuesta HTTP 500 (Internal Server Error) con el mensaje de la excepción
        // Esto asegura que el cliente reciba una respuesta estructurada en lugar de un stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}