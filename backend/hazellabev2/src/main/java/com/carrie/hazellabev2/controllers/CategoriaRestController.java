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

import com.carrie.hazellabev2.entities.Categoria;
import com.carrie.hazellabev2.services.CategoriaService;

/* ================= Controlador REST para Gestión de Categorías ================= */

// Habilita solicitudes cruzadas desde el frontend en desarrollo
@CrossOrigin(origins = "http://localhost:5173")
// Marca esta clase como controlador REST que maneja solicitudes HTTP y serializa respuestas a JSON
@RestController
// Define la ruta base para todos los endpoints de categorías
@RequestMapping("/api/categorias")

public class CategoriaRestController {    
    // Inyección automática del servicio de categorías para separar lógica de negocio del controlador
    @Autowired
    private CategoriaService categoriaService;
    
    /* ================= Endpoints CRUD para Categorías ================= */
    
    // Crea una nueva categoría en el sistema. POST /api/categorias - Recibe los datos de la categoría en el cuerpo de la solicitud
    @PostMapping
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaService.crear(categoria);
        // Retorna HTTP 200 (OK) con la categoría recién creada incluyendo su ID generado
        return ResponseEntity.ok(nuevaCategoria);
    }

    // Obtiene una categoría específica basada en su identificador único. GET /api/categorias/{id} - Extrae el ID de la ruta URL como parámetro
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerPorID(id);
        // Retorna HTTP 200 (OK) con la categoría encontrada o lanza excepción si no existe
        return ResponseEntity.ok(categoria);
    }
  
    // Actualiza una categoría existente con nuevos datos. PUT /api/categorias/{id} - Combina el ID de la ruta con los datos actualizados del cuerpo
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoriaActualizada) {
        Categoria categoria = categoriaService.actualizar(id, categoriaActualizada);
        // Retorna HTTP 200 (OK) con la categoría actualizada
        return ResponseEntity.ok(categoria);
    }

    // Obtiene la lista completa de todas las categorías disponibles en el sistema. GET /api/categorias - No requiere parámetros, devuelve colección completa
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = categoriaService.listarTodo();
        // Retorna HTTP 200 (OK) con la lista de categorías (puede estar vacía)
        return ResponseEntity.ok(categorias);
    }

    // Elimina permanentemente una categoría del sistema basado en su ID. DELETE /api/categorias/{id} - Elimina la categoría especificada
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        // Retorna HTTP 204 (No Content) indicando éxito en la eliminación sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }
}