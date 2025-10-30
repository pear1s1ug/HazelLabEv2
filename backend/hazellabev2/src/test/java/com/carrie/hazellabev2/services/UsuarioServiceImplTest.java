package com.carrie.hazellabev2.services;

import com.carrie.hazellabev2.entities.Usuario;
import com.carrie.hazellabev2.repositories.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/* ================= Pruebas Unitarias para Servicio de Usuarios ================= */

/**
 * Pruebas unitarias completas para UsuarioServiceImpl.
 * No requiere base de datos ni contexto de Spring Boot.
 * Utiliza Mockito para simular dependencias y aislar el código bajo prueba.
 */
class UsuarioServiceImplTest {
    // Simula el repositorio de usuarios para evitar acceso real a base de datos
    @Mock
    private UsuarioRepository usuarioRepository;

    // Simula el codificador de contraseñas para pruebas de seguridad
    @Mock
    private PasswordEncoder passwordEncoder;

    // Inyecta los mocks en el servicio real que se está probando
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    // Usuario de prueba reutilizable para múltiples casos de prueba
    private Usuario usuario;

    /* ================= Configuración Inicial ================= */
    
    // Configura el entorno de prueba antes de cada método de prueba
    @BeforeEach
    void setUp() {
        // Inicializa los mocks de Mockito
        MockitoAnnotations.openMocks(this);

        // Crea un usuario de prueba con datos completos y realistas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("carrie");
        usuario.setApellidos("hazel");
        usuario.setEmail("test@gmail.com");
        usuario.setPassword("1234");
        usuario.setRut("12345678-9");
        usuario.setRole("cliente");
        usuario.setStatus("activo");
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setRegion("RM");
        usuario.setComuna("Santiago");
        usuario.setDireccion("Calle Falsa 123");
        usuario.setFechaNacimiento("2000-01-01");
    }

    /* ================= PRUEBAS PARA CREACIÓN DE USUARIOS ================= */

    // Verifica que un usuario se crea exitosamente con todos los datos requeridos
    @Test
    void testCrearUsuario_Exitoso() {
        // Configura comportamiento simulado: codificador retorna contraseña encriptada y repositorio guarda usuario
        when(passwordEncoder.encode(any())).thenReturn("encrypted1234");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Ejecuta el método bajo prueba
        Usuario result = usuarioService.crear(usuario);

        // Verificaciones: usuario no nulo, contraseña encriptada, rol y estado correctos, repositorio llamado
        assertNotNull(result);
        assertEquals("encrypted1234", result.getPassword());
        assertEquals("cliente", result.getRole());
        assertEquals("activo", result.getStatus());
        verify(usuarioRepository).save(any());
    }

    // Verifica que se lanza excepción cuando se intenta crear usuario sin contraseña
    @Test
    void testCrearUsuario_SinPassword_LanzaExcepcion() {
        usuario.setPassword(null);
        // Ejecuta y verifica que lanza excepción con mensaje relacionado a contraseña
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(usuario));
        assertTrue(ex.getMessage().contains("contraseña"));
    }

    // Verifica validación de formato de email durante la creación de usuario
    @Test
    void testCrearUsuario_EmailInvalido_LanzaExcepcion() {
        usuario.setEmail("no_valido@otrodominio.com");
        // Ejecuta y verifica que lanza excepción con mensaje relacionado a correo
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(usuario));
        assertTrue(ex.getMessage().contains("correo"));
    }

    /* ================= PRUEBAS PARA OBTENCIÓN POR ID ================= */

    // Verifica recuperación exitosa de usuario existente por su ID
    @Test
    void testObtenerPorID_Exitoso() {
        // Configura repositorio simulado para retornar usuario cuando se busca por ID 1
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Usuario result = usuarioService.obtenerPorID(1L);
        // Verifica que el usuario retornado tiene el username esperado
        assertEquals("carrie", result.getUsername());
    }

    // Verifica manejo de caso cuando se busca un usuario con ID inexistente
    @Test
    void testObtenerPorID_NoExistente_LanzaExcepcion() {
        // Configura repositorio para retornar vacío cuando se busca ID 99
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Verifica que lanza excepción al buscar usuario no existente
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerPorID(99L));
    }

    /* ================= PRUEBAS PARA ACTUALIZACIÓN DE USUARIOS ================= */

    // Verifica actualización completa y exitosa de usuario existente
    @Test
    void testActualizarUsuario_Exitoso() {
        // Configura mocks: usuario existe, codificador funciona, repositorio guarda
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPass");
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Crea usuario con datos actualizados
        Usuario nuevo = new Usuario();
        nuevo.setUsername("nuevoUser");
        nuevo.setPassword("nuevaPass");
        nuevo.setEmail("nuevo@gmail.com");
        nuevo.setRut("87654321-0");
        nuevo.setRole("admin");
        nuevo.setStatus("activo");

        // Ejecuta actualización
        Usuario result = usuarioService.actualizar(1L, nuevo);

        // Verifica que los datos se actualizaron correctamente
        assertEquals("nuevoUser", result.getUsername());
        assertEquals("admin", result.getRole());
        assertEquals("encodedNewPass", result.getPassword());
        verify(usuarioRepository).save(any());
    }

    // Verifica validación de email durante actualización de usuario
    @Test
    void testActualizarUsuario_EmailInvalido_LanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Usuario nuevo = new Usuario();
        nuevo.setEmail("invalido@otro.com");
        // Verifica que lanza excepción con email inválido
        assertThrows(RuntimeException.class, () -> usuarioService.actualizar(1L, nuevo));
    }

    /* ================= PRUEBAS PARA ELIMINACIÓN DE USUARIOS ================= */

    // Verifica eliminación exitosa de usuario existente
    @Test
    void testEliminarUsuario_Exitoso() {
        // Configura repositorio para confirmar que usuario existe
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        // Ejecuta eliminación
        usuarioService.eliminar(1L);
        // Verifica que se llamó al método de eliminación con el ID correcto
        verify(usuarioRepository).deleteById(1L);
    }

    // Verifica manejo de eliminación de usuario no existente
    @Test
    void testEliminarUsuario_NoExistente_LanzaExcepcion() {
        // Configura repositorio para indicar que usuario no existe
        when(usuarioRepository.existsById(anyLong())).thenReturn(false);
        // Verifica que lanza excepción al intentar eliminar usuario no existente
        assertThrows(RuntimeException.class, () -> usuarioService.eliminar(99L));
    }

    /* ================= PRUEBAS PARA SISTEMA DE LOGIN ================= */

    // Verifica login exitoso con credenciales correctas y usuario activo
    @Test
    void testLogin_Exitoso() {
        // Configura mocks: usuario encontrado por email y contraseña coincide
        when(usuarioRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "1234")).thenReturn(true);

        // Ejecuta login
        Usuario result = usuarioService.login("test@gmail.com", "1234");
        // Verifica que retorna el usuario correcto
        assertEquals("carrie", result.getUsername());
    }

    // Verifica que login falla con contraseña incorrecta
    @Test
    void testLogin_PasswordIncorrecta_LanzaExcepcion() {
        when(usuarioRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // Ejecuta y verifica que lanza excepción de credenciales inválidas
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("test@gmail.com", "wrong"));
        assertTrue(ex.getMessage().contains("Credenciales"));
    }

    // Verifica que login falla con usuario inactivo (aunque credenciales sean correctas)
    @Test
    void testLogin_UsuarioInactivo_LanzaExcepcion() {
        // Configura usuario con estado inactivo
        usuario.setStatus("inactivo");
        when(usuarioRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // Ejecuta y verifica que lanza excepción por usuario inactivo
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("test@gmail.com", "1234"));
        assertTrue(ex.getMessage().contains("inactivo"));
    }

    /* ================= PRUEBAS PARA BÚSQUEDAS Y FILTROS ================= */

    // Verifica búsqueda de usuarios por fragmento de username (case-insensitive)
    @Test
    void testBuscarPorUsername() {
        // Configura repositorio para retornar lista con usuario cuando se busca por "car"
        when(usuarioRepository.findByUsernameContainingIgnoreCase("car"))
                .thenReturn(Arrays.asList(usuario));
        List<Usuario> result = usuarioService.buscarPorUsername("car");
        // Verifica que retorna exactamente un usuario
        assertEquals(1, result.size());
    }

    // Verifica filtrado de usuarios por rol específico
    @Test
    void testBuscarPorRol() {
        // Configura repositorio para retornar usuarios con rol "cliente"
        when(usuarioRepository.findByRole("cliente"))
                .thenReturn(Arrays.asList(usuario));
        List<Usuario> result = usuarioService.buscarPorRol("cliente");
        // Verifica que retorna exactamente un usuario
        assertEquals(1, result.size());
    }

    // Verifica filtrado de usuarios por estado específico
    @Test
    void testBuscarPorEstado() {
        // Configura repositorio para retornar usuarios con estado "activo"
        when(usuarioRepository.findByStatus("activo"))
                .thenReturn(Arrays.asList(usuario));
        List<Usuario> result = usuarioService.buscarPorEstado("activo");
        // Verifica que retorna exactamente un usuario
        assertEquals(1, result.size());
    }
}