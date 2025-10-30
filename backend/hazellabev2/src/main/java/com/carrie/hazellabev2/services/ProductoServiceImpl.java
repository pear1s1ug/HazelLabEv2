package com.carrie.hazellabev2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrie.hazellabev2.entities.Producto;
import com.carrie.hazellabev2.repositories.ProductoRepository;

/* ================= Servicio de Implementación para Gestión de Productos ================= */

@Service

public class ProductoServiceImpl implements ProductoService {
    // Inyección del repositorio para acceso a datos de productos
    @Autowired
    private ProductoRepository productoRepository;

    /* ================= OPERACIONES CRUD BÁSICAS ================= */
    
    // Crea un nuevo producto en el inventario - persiste la entidad completa con todos sus atributos
    @Override
    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    // Obtiene un producto específico por su ID - lanza excepción si no existe para evitar null pointers
    @Override
    public Producto obtenerPorID(Long id) {
        return productoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado."));
    }    

    // Actualiza un producto existente - verifica existencia y actualiza TODOS los campos editables
    @Override
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto productoExistente = obtenerPorID(id);

        // Actualización completa de todos los campos editables del producto
        productoExistente.setName(productoActualizado.getName());
        productoExistente.setBatchCode(productoActualizado.getBatchCode());
        productoExistente.setDescription(productoActualizado.getDescription());
        productoExistente.setChemCode(productoActualizado.getChemCode());
        productoExistente.setExpDate(productoActualizado.getExpDate());
        productoExistente.setElabDate(productoActualizado.getElabDate());
        productoExistente.setCost(productoActualizado.getCost());
        productoExistente.setStock(productoActualizado.getStock());
        productoExistente.setCategory(productoActualizado.getCategory());
        productoExistente.setImage(productoActualizado.getImage());
        productoExistente.setActiveStatus(productoActualizado.getActiveStatus());
        productoExistente.setStockCritico(productoActualizado.getStockCritico());
        productoExistente.setProveedor(productoActualizado.getProveedor());
        
        // Campo para productos destacados en secciones promocionales
        productoExistente.setDestacado(productoActualizado.getDestacado());
        
        // creationDate NO se actualiza - se preserva la fecha original de creación
        // expirationDate se actualiza si es necesario (productos con nueva fecha de expiración)
        
        return productoRepository.save(productoExistente);
    }

    // Obtiene todos los productos del sistema - conversión necesaria por herencia de CrudRepository
    @Override
    public List<Producto> listarTodo() {
        return (List<Producto>) productoRepository.findAll();
    }
    
    // Elimina un producto del inventario - verifica existencia previa para evitar errores
    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado.");
        } 
        productoRepository.deleteById(id);
    }

    /* ================= OPERACIONES ESPECÍFICAS DE NEGOCIO ================= */
    
    // Desactiva un producto cambiando su estado a inactivo (soft delete)
    @Override
    public Producto desactivar(Long id) {
        Producto producto = obtenerPorID(id);
        producto.setActiveStatus(false);
        return productoRepository.save(producto);
    }

    // Actualiza únicamente la URL de la imagen de un producto - operación específica para gestión de medios
    @Override
    public Producto actualizarImagen(Long id, String imageUrl) {
        Producto producto = obtenerPorID(id);
        producto.setImage(imageUrl);
        return productoRepository.save(producto);
    }

    // Obtiene productos marcados como destacados para secciones promocionales y página principal
    @Override
    public List<Producto> listarDestacados() {
        return productoRepository.findByDestacadoTrue();
    }

    /* ================= SISTEMA DE FILTRADO Y BÚSQUEDA ================= */
    
    // Busca productos por nombre (búsqueda parcial insensible a mayúsculas/minúsculas)
    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNameContainingIgnoreCase(nombre);
    }

    // Filtra productos por categoría específica usando ID de categoría
    @Override
    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoryId(categoriaId);
    }

    // Obtiene productos con stock bajo (menos de 5 unidades) para alertas de inventario
    @Override
    public List<Producto> buscarProductosStockBajo() {
        return productoRepository.findByStockLessThan(5);
    }

    // Filtra productos por estado activo/inactivo - útil para administración
    @Override
    public List<Producto> buscarPorEstado(Boolean activo) {
        return productoRepository.findByActiveStatus(activo);
    }

    // Búsqueda combinada por nombre Y categoría - filtrado avanzado para catálogo
    @Override
    public List<Producto> buscarPorNombreYCategoria(String nombre, Long categoriaId) {
        return productoRepository.findByNameContainingIgnoreCaseAndCategoryId(nombre, categoriaId);
    }

    // Búsqueda combinada por nombre Y estado - útil para búsquedas administrativas
    @Override
    public List<Producto> buscarPorNombreYEstado(String nombre, Boolean activo) {
        return productoRepository.findByNameContainingIgnoreCaseAndActiveStatus(nombre, activo);
    }
}