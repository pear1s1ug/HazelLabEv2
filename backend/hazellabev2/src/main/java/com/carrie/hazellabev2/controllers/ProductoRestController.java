package com.carrie.hazellabev2.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrie.hazellabev2.entities.Producto;
import com.carrie.hazellabev2.services.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* ================= Controlador REST para Gestión de Productos ================= */

// Habilita solicitudes cruzadas desde el frontend en desarrollo
@CrossOrigin(origins = "http://localhost:5173")
// Marca esta clase como controlador REST que maneja solicitudes HTTP y serializa respuestas a JSON
@RestController
// Define la ruta base para todos los endpoints de productos
@RequestMapping("/api/productos")
// Documentación Swagger/OpenAPI para agrupar endpoints relacionados en la UI
@Tag(name = "Productos", description = "Operaciones CRUD para gestión de productos del inventario")

public class ProductoRestController {
    
    // Inyección automática del servicio de productos para separar lógica de negocio del controlador
    @Autowired
    private ProductoService productoService;
    
    /* ================= Endpoints CRUD Básicos para Productos ================= */
    
    // Crea un nuevo producto en el inventario. POST /api/productos - Recibe los datos del producto en el cuerpo de la solicitud
    @Operation(summary = "Crear nuevo producto", description = "Agrega un nuevo producto al inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de producto inválidos")
    })
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crear(producto);
        // Retorna HTTP 200 (OK) con el producto recién creado incluyendo su ID generado
        return ResponseEntity.ok(nuevoProducto);
    }

    // Obtiene un producto específico basado en su identificador único. GET /api/productos/{id} - Extrae el ID de la ruta URL
    @Operation(summary = "Obtener producto por ID", description = "Recupera la información de un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorID(id);
        // Retorna HTTP 200 (OK) con el producto encontrado
        return ResponseEntity.ok(producto);
    }
  
    // Actualiza completamente un producto existente. PUT /api/productos/{id} - Combina ID de la ruta con datos actualizados
    @Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        Producto producto = productoService.actualizar(id, productoActualizado);
        // Retorna HTTP 200 (OK) con el producto actualizado
        return ResponseEntity.ok(producto);
    }

    // Obtiene la lista completa de todos los productos del inventario. GET /api/productos - No requiere parámetros
    @Operation(summary = "Listar todos los productos", description = "Obtiene la lista completa de productos del inventario")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.listarTodo();
        // Retorna HTTP 200 (OK) con la lista completa de productos
        return ResponseEntity.ok(productos);
    }

    // Elimina permanentemente un producto del inventario. DELETE /api/productos/{id} - Elimina el producto especificado
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        // Retorna HTTP 204 (No Content) indicando éxito en la eliminación sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }

    /* ================= Endpoints de Gestión de Estado ================= */
    
    // Desactiva un producto cambiando su estado a inactivo (soft delete). PATCH /api/productos/{id}/desactivar
    @Operation(summary = "Desactivar producto", description = "Desactiva un producto (cambia su estado a inactivo)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Producto> desactivar(@PathVariable Long id) {
        // Retorna HTTP 200 (OK) con el producto desactivado
        return ResponseEntity.ok(productoService.desactivar(id));
    }

    /* ================= Endpoints de Gestión de Imágenes ================= */
    
    // Actualiza la URL de la imagen de un producto específico. POST /api/productos/{id}/upload-image - Recibe URL como parámetro
    @Operation(summary = "Actualizar imagen de producto", description = "Actualiza la URL de la imagen de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "URL de imagen inválida"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<Producto> uploadImage(@PathVariable Long id, @RequestParam("imageUrl") String imageUrl) {
        try {
            // Validaciones de seguridad para la URL de la imagen
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                throw new RuntimeException("La URL de la imagen es requerida");
            }

            // Verifica que la URL tenga formato válido (protocolo HTTP/HTTPS)
            if (!imageUrl.startsWith("http")) {
                throw new RuntimeException("La URL debe comenzar con http o https");
            }

            // Obtiene el producto existente y actualiza solo la imagen
            Producto producto = productoService.obtenerPorID(id);
            producto.setImage(imageUrl);
            Producto productoActualizado = productoService.actualizar(id, producto);

            // Retorna HTTP 200 (OK) con el producto actualizado
            return ResponseEntity.ok(productoActualizado);

        } catch (RuntimeException e) {
            // Propaga excepción con mensaje descriptivo para el cliente
            throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage());
        }
    }   

    /* ================= Endpoints de Características Especiales ================= */
    
    // Obtiene productos marcados como destacados para mostrar en secciones promocionales
    @Operation(summary = "Listar productos destacados", description = "Obtiene la lista de productos marcados como destacados")
    @ApiResponse(responseCode = "200", description = "Lista de productos destacados obtenida exitosamente")
    @GetMapping("/destacados")
    public ResponseEntity<List<Producto>> listarDestacados() {
        List<Producto> productos = productoService.listarDestacados();
        // Retorna HTTP 200 (OK) con la lista de productos destacados
        return ResponseEntity.ok(productos);
    }

    /* ================= Endpoints de Filtrado y Búsqueda Avanzada ================= */
    
    // Busca productos cuyo nombre contenga el texto especificado (búsqueda parcial)
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos cuyo nombre contenga el texto especificado")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    // Filtra productos por categoría específica usando el ID de categoría
    @Operation(summary = "Buscar productos por categoría", description = "Filtra productos por categoría específica")
    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestParam Long categoriaId) {
        List<Producto> productos = productoService.buscarPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    // Obtiene productos con stock bajo (menor a 5 unidades) para alertas de inventario
    @Operation(summary = "Productos con stock bajo", description = "Obtiene productos con stock menor a 5 unidades")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerProductosStockBajo() {
        List<Producto> productos = productoService.buscarProductosStockBajo();
        return ResponseEntity.ok(productos);
    }

    // Filtra productos por estado activo/inactivo para gestión administrativa
    @Operation(summary = "Buscar productos por estado", description = "Filtra productos por estado activo/inactivo")
    @GetMapping("/buscar/estado")
    public ResponseEntity<List<Producto>> buscarPorEstado(@RequestParam Boolean activo) {
        List<Producto> productos = productoService.buscarPorEstado(activo);
        return ResponseEntity.ok(productos);
    }

    // Búsqueda avanzada que combina múltiples criterios (nombre y categoría)
    @Operation(summary = "Búsqueda avanzada", description = "Busca productos por nombre y categoría")
    @GetMapping("/buscar/avanzada")
    public ResponseEntity<List<Producto>> buscarAvanzada(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId) {
        
        List<Producto> productos;
        // Lógica para determinar qué tipo de búsqueda ejecutar basado en parámetros proporcionados
        if (nombre != null && categoriaId != null) {
            productos = productoService.buscarPorNombreYCategoria(nombre, categoriaId);
        } else if (nombre != null) {
            productos = productoService.buscarPorNombre(nombre);
        } else if (categoriaId != null) {
            productos = productoService.buscarPorCategoria(categoriaId);
        } else {
            productos = productoService.listarTodo();
        }
        
        // Retorna HTTP 200 (OK) con los resultados de la búsqueda
        return ResponseEntity.ok(productos);
    }
}