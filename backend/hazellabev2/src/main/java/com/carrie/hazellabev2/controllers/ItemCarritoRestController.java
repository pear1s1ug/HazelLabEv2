package com.carrie.hazellabev2.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carrie.hazellabev2.entities.ItemCarrito;
import com.carrie.hazellabev2.services.ItemCarritoService;

/* ================= Controlador REST para Gestión de Items del Carrito ================= */

// Habilita solicitudes cruzadas desde el frontend en desarrollo
@CrossOrigin(origins = "http://localhost:5173")
// Marca esta clase como controlador REST que maneja solicitudes HTTP y serializa respuestas a JSON
@RestController
// Define la ruta base para todos los endpoints de items del carrito
@RequestMapping("/api/itemscarrito")

public class ItemCarritoRestController {

    // Inyección automática del servicio de items del carrito para separar lógica de negocio del controlador
    @Autowired
    private ItemCarritoService itemCarritoService;

    /* ================= Endpoints CRUD Básicos para Items del Carrito ================= */
    
    // Crea un nuevo item en el carrito de compras. POST /api/itemscarrito - Recibe los datos del item en el cuerpo de la solicitud
    @PostMapping
    public ResponseEntity<ItemCarrito> crearItemCarrito(@RequestBody ItemCarrito itemCarrito) {
        ItemCarrito nuevoItemCarrito = itemCarritoService.crear(itemCarrito);
        // Retorna HTTP 200 (OK) con el item del carrito recién creado incluyendo su ID generado
        return ResponseEntity.ok(nuevoItemCarrito);
    }

    // Obtiene un item específico del carrito basado en su identificador único. GET /api/itemscarrito/{id} - Extrae el ID de la ruta URL como parámetro
    @GetMapping("/{id}")
    public ResponseEntity<ItemCarrito> obtenerItemCarritoPorId(@PathVariable Long id) {
        ItemCarrito itemCarrito = itemCarritoService.obtenerPorID(id);
        // Retorna HTTP 200 (OK) con el item del carrito encontrado
        return ResponseEntity.ok(itemCarrito);
    }

    // Obtiene la lista completa de todos los items del carrito en el sistema (modo administrador). GET /api/itemscarrito - No requiere parámetros
    @GetMapping
    public ResponseEntity<List<ItemCarrito>> listarItemsCarrito() {
        List<ItemCarrito> itemsCarrito = itemCarritoService.listarTodo();
        // Retorna HTTP 200 (OK) con la lista completa de items del carrito
        return ResponseEntity.ok(itemsCarrito);
    }

    // Elimina permanentemente un item del carrito basado en su ID. DELETE /api/itemscarrito/{id} - Elimina el item especificado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItemCarrito(@PathVariable Long id) {
        itemCarritoService.eliminar(id);
        // Retorna HTTP 204 (No Content) indicando éxito en la eliminación sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }

    /* ================= Endpoints Específicos para Funcionalidades de Carrito ================= */
    
    // Obtiene todos los items del carrito pertenecientes a un usuario específico. GET /api/itemscarrito/usuario/{usuarioId} - Filtra por ID de usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItemCarrito>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ItemCarrito> items = itemCarritoService.listarPorUsuario(usuarioId);

        // Forzar la carga inicial de los productos asociados para evitar problemas de lazy loading
        // Esto asegura que la información del producto esté disponible en la respuesta JSON
        items.forEach(item -> {
            if (item.getProducto() != null) {
                item.getProducto().getName(); // Acción intencional para cargar el proxy
            }
        });

        // Retorna HTTP 200 (OK) con la lista de items del carrito del usuario específico
        return ResponseEntity.ok(items);
    }

    // Actualiza completamente un item existente del carrito (incluyendo producto, cantidad, etc.). PUT /api/itemscarrito/{id} - Combina ID con datos actualizados
    @PutMapping("/{id}")
    public ResponseEntity<ItemCarrito> actualizarItemCarrito(
            @PathVariable Long id,
            @RequestBody ItemCarrito itemCarritoActualizado) {

        ItemCarrito itemCarrito = itemCarritoService.actualizar(id, itemCarritoActualizado);
        // Retorna HTTP 200 (OK) con el item del carrito actualizado
        return ResponseEntity.ok(itemCarrito);
    }

    // Actualiza únicamente la cantidad de un item específico del carrito. PUT /api/itemscarrito/{id}/cantidad - Modificación parcial optimizada
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<ItemCarrito> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody ItemCarrito item) {

        ItemCarrito actualizado = itemCarritoService.actualizarCantidad(id, item.getQuantity());
        // Retorna HTTP 200 (OK) con el item del carrito actualizado solo en cantidad
        return ResponseEntity.ok(actualizado);
    }
}