package com.carrie.hazellabev2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrie.hazellabev2.entities.ItemCarrito;
import com.carrie.hazellabev2.repositories.ItemCarritoRepository;

/* ================= Servicio de Implementación para Gestión de Items del Carrito ================= */

@Service

public class ItemCarritoServiceImpl implements ItemCarritoService {
    // Inyección del repositorio para acceso a datos de items del carrito
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    /* ================= OPERACIONES CRUD BÁSICAS ================= */
    
    // Crea un nuevo item en el carrito - persiste la relación usuario-producto-cantidad
    @Override
    public ItemCarrito crear(ItemCarrito itemCarrito) {
        return itemCarritoRepository.save(itemCarrito);
    }; 

    // Obtiene un item específico del carrito por su ID - lanza excepción si no existe
    @Override
    public ItemCarrito obtenerPorID(Long id) {
        return itemCarritoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado."));
    };  

    // Actualiza un item existente del carrito - verifica existencia y actualiza campos permitidos
    @Override
    public ItemCarrito actualizar(Long id, ItemCarrito itemCarritoActualizado) {
        ItemCarrito itemCarritoExistente = obtenerPorID(id);
        // Actualiza únicamente la cantidad - estrategia de actualización granular
        itemCarritoExistente.setQuantity(itemCarritoActualizado.getQuantity());
        return itemCarritoRepository.save(itemCarritoExistente);
    };

    // Obtiene todos los items del carrito del sistema - conversión necesaria por CrudRepository
    @Override
    public List<ItemCarrito> listarTodo() {
        return (List<ItemCarrito>) itemCarritoRepository.findAll();
    };

    // Elimina un item del carrito por su ID - verifica existencia previa para evitar errores
    @Override
    public void eliminar(Long id) {
        if (!itemCarritoRepository.existsById(id)) {
            throw new RuntimeException("Item del carrito no encontrado.");
        } 
        itemCarritoRepository.deleteById(id);
    };

    /* ================= OPERACIONES ESPECÍFICAS DEL CARRITO ================= */
    
    // Obtiene todos los items del carrito pertenecientes a un usuario específico
    @Override
    public List<ItemCarrito> listarPorUsuario(Long usuarioId) {
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }

    // Actualiza únicamente la cantidad de un item específico 
    @Override
    public ItemCarrito actualizarCantidad(Long id, int nuevaCantidad) {
        ItemCarrito item = itemCarritoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        item.setQuantity(nuevaCantidad);
        return itemCarritoRepository.save(item);
    }
}