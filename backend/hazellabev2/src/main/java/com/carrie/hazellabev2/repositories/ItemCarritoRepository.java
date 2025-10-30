package com.carrie.hazellabev2.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.carrie.hazellabev2.entities.ItemCarrito;

public interface ItemCarritoRepository extends CrudRepository<ItemCarrito, Long> {
    // Buscar por Id
    List<ItemCarrito> findByUsuarioId(Long usuarioId);
}
