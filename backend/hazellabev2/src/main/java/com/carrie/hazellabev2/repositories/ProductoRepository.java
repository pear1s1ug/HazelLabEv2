package com.carrie.hazellabev2.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.carrie.hazellabev2.entities.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Long> {
    /* ================= CONSULTAS POR CARACTERÍSTICAS ESPECIALES ================= */
    // Busca productos marcados como destacados para secciones promocionales
    List<Producto> findByDestacadoTrue();
    // Busca productos con stock bajo (menor al valor especificado) para alertas de inventario
    List<Producto> findByStockLessThan(int stock);

    /* ================= CONSULTAS POR ATRIBUTOS BÁSICOS ================= */
    // Busca productos cuyo nombre contenga el texto especificado (insensible a mayúsculas/minúsculas)
    List<Producto> findByNameContainingIgnoreCase(String name);
    // Busca productos por ID de categoría específica
    List<Producto> findByCategoryId(Long categoryId);
    // Busca productos por estado activo/inactivo
    List<Producto> findByActiveStatus(Boolean activeStatus);

    /* ================= CONSULTAS COMBINADAS Y BÚSQUEDAS AVANZADAS ================= */
    // Búsqueda combinada por nombre y categoría - filtrado avanzado
    List<Producto> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId);
    // Búsqueda combinada por nombre y estado - útil para administración
    List<Producto> findByNameContainingIgnoreCaseAndActiveStatus(String name, Boolean activeStatus);
}