package com.carrie.hazellabev2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carrie.hazellabev2.dto.LoginRequest;
import com.carrie.hazellabev2.entities.Usuario;
import com.carrie.hazellabev2.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* ================= Controlador REST para Autenticación de Usuarios ================= */

// Habilita solicitudes cruzadas desde el frontend en desarrollo
@CrossOrigin(origins = "http://localhost:5173")
// Marca esta clase como controlador REST que maneja solicitudes HTTP y serializa respuestas a JSON
@RestController
// Define la ruta base para todos los endpoints de autenticación
@RequestMapping("/api/auth")
// Documentación Swagger/OpenAPI para agrupar endpoints relacionados en la UI
@Tag(name = "Autenticación", description = "Operaciones de login y autenticación de usuarios")


public class LoginRestController {
    
    // Inyección automática del servicio de usuarios para manejar la lógica de autenticación
    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para autenticar usuarios en el sistema. POST /api/auth/login - Valida credenciales y establece sesión
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas o usuario inactivo"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica al usuario usando el servicio con las credenciales proporcionadas
            Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword());
            
            // Validación de seguridad: Solo usuarios con rol de administrador pueden acceder al dashboard. 
            if (!usuario.getRole().equals("super_admin") && !usuario.getRole().equals("admin")) {
                return ResponseEntity.badRequest().body("Acceso solo para administradores");
            }
            
            // Medida de seguridad: Elimina la contraseña del objeto antes de enviarlo al cliente. Previene exposición accidental de credenciales en la respuesta
            usuario.setPassword(null);
            
            // Retorna HTTP 200 (OK) con la información del usuario autenticado (sin contraseña)
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            // Manejo de errores: Captura excepciones del servicio y retorna error 400 con mensaje descriptivo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}