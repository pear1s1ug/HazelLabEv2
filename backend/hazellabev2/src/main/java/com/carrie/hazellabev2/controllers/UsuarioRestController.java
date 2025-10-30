package com.carrie.hazellabev2.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrie.hazellabev2.entities.Usuario;
import com.carrie.hazellabev2.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* ================= Controlador REST para Gestión de Usuarios ================= */

// Habilita solicitudes cruzadas desde el frontend en desarrollo
@CrossOrigin(origins = "http://localhost:5173")
// Marca esta clase como controlador REST que maneja solicitudes HTTP y serializa respuestas a JSON
@RestController
// Define la ruta base para todos los endpoints de usuarios
@RequestMapping("/api/usuarios")
// Documentación Swagger/OpenAPI para agrupar endpoints relacionados en la UI
@Tag(name = "Usuarios", description = "Operaciones CRUD para gestión de usuarios")

public class UsuarioRestController {
    // Inyección automática del servicio de usuarios para separar lógica de negocio del controlador
    @Autowired
    private UsuarioService usuarioService;
    
    /* ================= Endpoints CRUD para Gestión de Usuarios ================= */
    
    // Crea un nuevo usuario en el sistema. POST /api/usuarios - Recibe los datos del usuario en el cuerpo de la solicitud
    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.crear(usuario);
            // Retorna HTTP 200 (OK) con el usuario recién creado incluyendo su ID generado
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            // Manejo de errores: Retorna error 400 con mensaje descriptivo para validaciones fallidas
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtiene un usuario específico basado en su identificador único. GET /api/usuarios/{id} - Extrae el ID de la ruta URL
    @Operation(summary = "Obtener usuario por ID", description = "Recupera la información de un usuario específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorID(id);
        // Retorna HTTP 200 (OK) con el usuario encontrado
        return ResponseEntity.ok(usuario);
    }
  
    // Actualiza completamente un usuario existente. PUT /api/usuarios/{id} - Combina ID de la ruta con datos actualizados
    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario usuario = usuarioService.actualizar(id, usuarioActualizado);
            // Retorna HTTP 200 (OK) con el usuario actualizado
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            // Manejo de errores: Retorna error 400 para validaciones o reglas de negocio fallidas
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtiene la lista completa de todos los usuarios registrados en el sistema. GET /api/usuarios - No requiere parámetros
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                 content = @Content(mediaType = "application/json", 
                 schema = @Schema(implementation = Usuario.class)))
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodo();
        // Retorna HTTP 200 (OK) con la lista completa de usuarios
        return ResponseEntity.ok(usuarios);
    }

    // Elimina permanentemente un usuario del sistema. DELETE /api/usuarios/{id} - Elimina el usuario especificado
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        // Retorna HTTP 204 (No Content) indicando éxito en la eliminación sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }
}