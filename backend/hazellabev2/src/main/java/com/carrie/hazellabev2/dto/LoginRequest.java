package com.carrie.hazellabev2.dto;

/* LoginRequest comunica explícitamente que es una solicitud de autenticación, no una operación CRUD sobre usuarios
    Evita exponer toda la estructura de la entidad Usuario que puede contener información sensible o campos internos que no deben ser manipulados desde el cliente.
    contiene SOLO los campos necesarios para login.
    Permite cambiar la estructura de la entidad Usuario sin afectar la API de login. El DTO actúa como contrato estable entre frontend y backend
    Puede tener validaciones específicas para login sin contaminar la entidad principal con anotaciones de validación de contexto */

public class LoginRequest {
    // Atributos
    private String email;
    private String password;

    // Getters y setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

