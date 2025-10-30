package com.carrie.hazellabev2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrie.hazellabev2.entities.Categoria;
import com.carrie.hazellabev2.repositories.CategoriaRepository;

/* ================= Implementación de Servicio para Gestión de Categorías ================= */

@Service

public class CategoriaServiceImpl implements CategoriaService {
    // Inyección del repositorio para acceso a datos de categorías
    @Autowired
    private CategoriaRepository categoriaRepository;

    /* ================= OPERACIONES CRUD PARA CATEGORÍAS ================= */
    
    // Crea una nueva categoría en el sistema - persiste la entidad completa en la base de datos
    @Override
    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Obtiene una categoría específica por su ID - lanza excepción si no existe para evitar null pointers
    @Override
    public Categoria obtenerPorID(Long id) {
        return categoriaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoría no encontrada."));
    };  

    // Actualiza una categoría existente - verifica existencia y actualiza solo el campo nombre
    @Override
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        // Valida que la categoría exista antes de intentar actualización
        Categoria categoriaExistente = obtenerPorID(id);
        
        // Actualiza únicamente el nombre - estrategia de actualización granular
        categoriaExistente.setNombre(categoriaActualizada.getNombre());
        
        return categoriaRepository.save(categoriaExistente);
    };

    // Obtiene todas las categorías del sistema - conversión necesaria por herencia de CrudRepository
    @Override
    public List<Categoria> listarTodo() {
        return (List<Categoria>) categoriaRepository.findAll();
    };

    // Elimina una categoría por su ID - verifica existencia previa para evitar errores
    @Override
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada.");
        } 
        categoriaRepository.deleteById(id);
    };
}