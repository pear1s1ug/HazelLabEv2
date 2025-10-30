package com.carrie.hazellabev2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.carrie.hazellabev2.entities.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    /* ================= CONSULTAS POR CREDENCIALES Y IDENTIFICACIÓN ================= */
    // Busca un usuario por su dirección de email (campo único para login)
    Optional<Usuario> findByEmail(String email);
    // Busca un usuario por su RUT (identificador único chileno)
    Optional<Usuario> findByRut(String rut);

    /* ================= CONSULTAS POR ATRIBUTOS BÁSICOS ================= */
    // Busca usuarios cuyo nombre de usuario contenga el texto especificado (insensible a mayúsculas/minúsculas)
    List<Usuario> findByUsernameContainingIgnoreCase(String username);
    // Busca usuarios por rol específico (admin, user, super_admin, etc.)
    List<Usuario> findByRole(String role);
    // Busca usuarios por estado (activo, inactivo, pendiente, etc.)
    List<Usuario> findByStatus(String status);

    /* ================= CONSULTAS DE BÚSQUEDA AVANZADA ================= */
    // Búsqueda combinada por nombre de usuario O email (insensible a mayúsculas/minúsculas)
    List<Usuario> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    
}