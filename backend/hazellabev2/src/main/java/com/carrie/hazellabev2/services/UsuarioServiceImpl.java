package com.carrie.hazellabev2.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carrie.hazellabev2.entities.Usuario;
import com.carrie.hazellabev2.repositories.UsuarioRepository;

/* ================= Servicio de Implementación para Gestión de Usuarios ================= */

@Service

public class UsuarioServiceImpl implements UsuarioService {

    // Inyección del repositorio para acceso a datos de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Inyección del codificador de contraseñas para seguridad de credenciales
    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ================= OPERACIONES CRUD CON VALIDACIONES ================= */
    
    // Crea un nuevo usuario en el sistema - incluye validaciones completas y encriptación de contraseña
    @Override
    public Usuario crear(Usuario usuario) {
        // Validaciones de campos obligatorios para integridad de datos
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña no puede ser nula o vacía");
        }

        if (usuario.getRut() == null || usuario.getRut().isEmpty()) {
            throw new RuntimeException("El RUT es obligatorio");
        }

        // Validación de dominio de email permitido (política institucional)
        if (!validarEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com");
        }

        // Establecimiento de valores por defecto para nuevos usuarios
        usuario.setRole(usuario.getRole() != null ? usuario.getRole() : "cliente");
        usuario.setStatus(usuario.getStatus() != null ? usuario.getStatus() : "activo");
        usuario.setCreatedAt(usuario.getCreatedAt() != null ? usuario.getCreatedAt() : LocalDateTime.now());

        // Inicialización de campos opcionales para evitar valores nulos
        if (usuario.getApellidos() == null) {
            usuario.setApellidos("");
        }
        if (usuario.getDireccion() == null) {
            usuario.setDireccion("");
        }

        // Encriptación segura de la contraseña antes de almacenar
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        return usuarioRepository.save(usuario);
    }

    // Obtiene un usuario específico por su ID - lanza excepción si no existe
    @Override
    public Usuario obtenerPorID(Long id) {
        return usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    };  

    // Actualiza un usuario existente - incluye validaciones y actualización condicional de contraseña
    @Override
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = obtenerPorID(id);
        
        // Actualización completa de todos los campos editables del perfil de usuario
        usuarioExistente.setUsername(usuarioActualizado.getUsername());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setRut(usuarioActualizado.getRut());
        usuarioExistente.setRole(usuarioActualizado.getRole());
        usuarioExistente.setStatus(usuarioActualizado.getStatus());
        usuarioExistente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
        usuarioExistente.setRegion(usuarioActualizado.getRegion());
        usuarioExistente.setComuna(usuarioActualizado.getComuna());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
        
        // Re-validación de email durante actualización para mantener políticas
        if (usuarioActualizado.getEmail() != null && !validarEmail(usuarioActualizado.getEmail())) {
            throw new RuntimeException("El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com");
        }
        
        // Actualización condicional de contraseña - solo si se proporciona una nueva y es diferente
        if (usuarioActualizado.getPassword() != null && 
            !usuarioActualizado.getPassword().isEmpty() &&
            !passwordEncoder.matches(usuarioActualizado.getPassword(), usuarioExistente.getPassword())) {
            
            String passwordEncriptada = passwordEncoder.encode(usuarioActualizado.getPassword());
            usuarioExistente.setPassword(passwordEncriptada);
        }
        
        return usuarioRepository.save(usuarioExistente);
    };

    // Obtiene todos los usuarios del sistema - conversión necesaria por CrudRepository
    @Override
    public List<Usuario> listarTodo() {
        return (List<Usuario>) usuarioRepository.findAll();
    }; 

    // Elimina un usuario del sistema - verifica existencia previa para evitar errores
    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado.");
        } 
        usuarioRepository.deleteById(id);
    };

    /* ================= SISTEMA DE AUTENTICACIÓN Y SEGURIDAD ================= */
    
    // Valida si una contraseña en texto plano coincide con la versión encriptada almacenada
    @Override
    public boolean validarPassword(String passwordPlano, String passwordEncriptado) {
        return passwordEncoder.matches(passwordPlano, passwordEncriptado);
    }

    // Busca usuario por email - utilizado principalmente para procesos de login
    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Proceso completo de autenticación de usuarios - valida credenciales y estado
    @Override
    public Usuario login(String email, String password) {
        // Buscar usuario por email en la base de datos
        Usuario usuario = findByEmail(email);

        // Verificación de contraseña usando encriptación segura
        if (!validarPassword(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas.");
        }

        // Verificación de estado del usuario (solo usuarios activos pueden acceder)
        if (!usuario.getStatus().equals("activo")) {
            throw new RuntimeException("Usuario inactivo.");
        }

        return usuario;
    }

    /* ================= VALIDACIONES Y UTILIDADES ================= */
    
    // Valida que el email pertenezca a los dominios permitidos (política de la institución)
    private boolean validarEmail(String email) {
        if (email == null) return false;
        return email.endsWith("@duoc.cl") || 
               email.endsWith("@profesor.duoc.cl") || 
               email.endsWith("@gmail.com");
    }

    /* ================= SISTEMA DE BÚSQUEDA Y FILTRADO ================= */
    
    // Busca usuarios por nombre de usuario (búsqueda parcial insensible a mayúsculas/minúsculas)
    @Override
    public List<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(username);
    }

    // Filtra usuarios por rol específico (admin, cliente, super_admin, etc.)
    @Override
    public List<Usuario> buscarPorRol(String role) {
        return usuarioRepository.findByRole(role);
    }

    // Filtra usuarios por estado (activo, inactivo, suspendido, etc.)
    @Override
    public List<Usuario> buscarPorEstado(String status) {
        return usuarioRepository.findByStatus(status);
    }
}