package com.carrie.hazellabev2.controllers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.carrie.hazellabev2.entities.Usuario;
import com.carrie.hazellabev2.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

/* ================= Pruebas de Integración para Controlador de Usuarios ================= */

/**
 * Pruebas de integración para UsuarioRestController.
 * Evalúa endpoints HTTP reales simulando peticiones web sin levantar servidor completo.
 * Combina Spring Boot Test con MockMvc para testing de capa web.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioRestControllerTest {
    // Cliente MVC simulado para realizar peticiones HTTP a los endpoints
    @Autowired
    private MockMvc mockMvc;

    // Utilidad para convertir objetos Java a JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper;

    // Simula el servicio de usuarios para aislar la capa de controlador
    @MockBean
    private UsuarioService usuarioService;

    // Lista de usuarios de prueba reutilizable
    private List<Usuario> usuariosLista;

    /* ================= PRUEBAS PARA ENDPOINT DE LISTADO ================= */

    // Verifica que el endpoint GET /api/usuarios retorna lista completa de usuarios
    @Test
    public void listarUsuariosTest() throws Exception {
        // Configurar datos de prueba usando SETTERS
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("admin");
        usuario1.setEmail("admin@duoc.cl");
        usuario1.setRut("12345678-9");
        usuario1.setPassword("password123");
        usuario1.setRole("super_admin");
        usuario1.setStatus("activo");
        usuario1.setCreatedAt(LocalDateTime.now());
        usuario1.setRegion("Metropolitana");
        usuario1.setComuna("Santiago");
        usuario1.setApellidos("Administrador");
        usuario1.setFechaNacimiento("1985-05-15");
        usuario1.setDireccion("Av. Siempre Viva 123");
        
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("vendedor1");
        usuario2.setEmail("vendedor1@duoc.cl");
        usuario2.setRut("98765432-1");
        usuario2.setPassword("password456");
        usuario2.setRole("vendedor");
        usuario2.setStatus("activo");
        usuario2.setCreatedAt(LocalDateTime.now());
        usuario2.setRegion("Valparaíso");
        usuario2.setComuna("Viña del Mar");
        usuario2.setApellidos("Vendedor Principal");
        usuario2.setFechaNacimiento("1990-08-20");
        usuario2.setDireccion("Calle 456");
        
        usuariosLista = Arrays.asList(usuario1, usuario2);

        // Configurar mock del servicio para retornar lista de usuarios
        when(usuarioService.listarTodo()).thenReturn(usuariosLista);

        // Ejecutar petición GET y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE OBTENCIÓN POR ID ================= */

    // Verifica obtención exitosa de usuario específico por ID
    @Test
    public void obtenerUsuarioPorIdTest() {
        // Configurar usuario de prueba usando SETTERS
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setEmail("admin@duoc.cl");
        usuario.setRut("12345678-9");
        usuario.setPassword("password123");
        usuario.setRole("super_admin");
        usuario.setStatus("activo");
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setRegion("Metropolitana");
        usuario.setComuna("Santiago");
        usuario.setApellidos("Administrador");
        usuario.setFechaNacimiento("1985-05-15");
        usuario.setDireccion("Av. Siempre Viva 123");

        try {
            // Configurar mock del servicio para retornar usuario específico
            when(usuarioService.obtenerPorID(1L)).thenReturn(usuario);

            // Ejecutar petición GET con ID y verificar respuesta HTTP 200 (OK)
            mockMvc.perform(get("/api/usuarios/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception ex) {
            fail("El testing lanzó un error: " + ex.getMessage());
        }
    }

    // Verifica manejo adecuado cuando se busca usuario con ID inexistente
    @Test
    public void usuarioNoExisteTest() throws Exception {
        // Configurar mock del servicio para lanzar excepción de usuario no encontrado
        when(usuarioService.obtenerPorID(99L))
                .thenThrow(new RuntimeException("Usuario no encontrado."));

        // Ejecutar petición GET y verificar que retorna error HTTP 500 (Internal Server Error)
        mockMvc.perform(get("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE CREACIÓN ================= */

    // Verifica creación exitosa de nuevo usuario con datos válidos
    @Test
    public void crearUsuarioTest() throws Exception {
        // Configurar usuario nuevo (sin ID) usando SETTERS
        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setUsername("nuevoUsuario");
        usuarioNuevo.setEmail("nuevo@duoc.cl");
        usuarioNuevo.setRut("12345678-9");
        usuarioNuevo.setPassword("password123");
        usuarioNuevo.setRole("vendedor");
        usuarioNuevo.setStatus("activo");
        usuarioNuevo.setRegion("Metropolitana");
        usuarioNuevo.setComuna("Santiago");
        usuarioNuevo.setApellidos("Nuevo Apellido");
        usuarioNuevo.setFechaNacimiento("1995-12-25");
        usuarioNuevo.setDireccion("Av. Nueva 789");
        
        // Configurar usuario guardado (con ID) usando SETTERS
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(3L);
        usuarioGuardado.setUsername("nuevoUsuario");
        usuarioGuardado.setEmail("nuevo@duoc.cl");
        usuarioGuardado.setRut("12345678-9");
        usuarioGuardado.setPassword("passwordEncriptado");
        usuarioGuardado.setRole("vendedor");
        usuarioGuardado.setStatus("activo");
        usuarioGuardado.setCreatedAt(LocalDateTime.now());
        usuarioGuardado.setRegion("Metropolitana");
        usuarioGuardado.setComuna("Santiago");
        usuarioGuardado.setApellidos("Nuevo Apellido");
        usuarioGuardado.setFechaNacimiento("1995-12-25");
        usuarioGuardado.setDireccion("Av. Nueva 789");

        // Configurar mock del servicio para retornar usuario creado
        when(usuarioService.crear(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Ejecutar petición POST con datos JSON y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioNuevo)))
                .andExpect(status().isOk());
    }

    // Verifica validación de campo RUT obligatorio durante creación
    @Test
    public void crearUsuarioSinRutTest() throws Exception {
        // Configurar usuario sin RUT usando SETTERS
        Usuario usuarioSinRut = new Usuario();
        usuarioSinRut.setUsername("nuevoUsuario");
        usuarioSinRut.setEmail("nuevo@duoc.cl");
        usuarioSinRut.setRut(null); // RUT nulo
        usuarioSinRut.setPassword("password123");
        usuarioSinRut.setRole("vendedor");
        usuarioSinRut.setStatus("activo");

        // Configurar mock del servicio para lanzar excepción de RUT obligatorio
        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new RuntimeException("El RUT es obligatorio"));

        // Ejecutar petición POST y verificar error HTTP 400 (Bad Request)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioSinRut)))
                .andExpect(status().isBadRequest());
    }

    // Verifica validación de formato de email durante creación
    @Test
    public void crearUsuarioEmailInvalidoTest() throws Exception {
        // Configurar usuario con email inválido usando SETTERS
        Usuario usuarioEmailInvalido = new Usuario();
        usuarioEmailInvalido.setUsername("nuevoUsuario");
        usuarioEmailInvalido.setEmail("nuevo@yahoo.com"); // Email inválido
        usuarioEmailInvalido.setRut("12345678-9");
        usuarioEmailInvalido.setPassword("password123");
        usuarioEmailInvalido.setRole("vendedor");
        usuarioEmailInvalido.setStatus("activo");

        // Configurar mock del servicio para lanzar excepción de email inválido
        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new RuntimeException("El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com"));

        // Ejecutar petición POST y verificar error HTTP 400 (Bad Request)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioEmailInvalido)))
                .andExpect(status().isBadRequest());
    }

    // Verifica validación de campo password obligatorio durante creación
    @Test
    public void crearUsuarioSinPasswordTest() throws Exception {
        // Configurar usuario sin password usando SETTERS
        Usuario usuarioSinPassword = new Usuario();
        usuarioSinPassword.setUsername("nuevoUsuario");
        usuarioSinPassword.setEmail("nuevo@duoc.cl");
        usuarioSinPassword.setRut("12345678-9");
        usuarioSinPassword.setPassword(null); // Password nulo
        usuarioSinPassword.setRole("vendedor");
        usuarioSinPassword.setStatus("activo");

        // Configurar mock del servicio para lanzar excepción de password obligatorio
        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new RuntimeException("La contraseña no puede ser nula o vacía"));

        // Ejecutar petición POST y verificar error HTTP 400 (Bad Request)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioSinPassword)))
                .andExpect(status().isBadRequest());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE ELIMINACIÓN ================= */

    // Verifica eliminación exitosa de usuario existente
    @Test
    public void eliminarUsuarioTest() throws Exception {
        // Configurar usuario existente usando SETTERS
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setEmail("admin@duoc.cl");
        usuario.setRut("12345678-9");
        usuario.setPassword("password123");
        usuario.setRole("super_admin");
        usuario.setStatus("activo");
        usuario.setCreatedAt(LocalDateTime.now());

        // Configurar mock para verificar que usuario existe
        when(usuarioService.obtenerPorID(1L)).thenReturn(usuario);

        // Ejecutar petición DELETE y verificar respuesta HTTP 204 (No Content)
        mockMvc.perform(delete("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Verifica manejo de eliminación de usuario no existente
    @Test
    public void eliminarUsuarioNoExisteTest() throws Exception {
        // Configurar mock del servicio para lanzar excepción al eliminar usuario inexistente
        doThrow(new RuntimeException("Usuario no encontrado."))
                .when(usuarioService).eliminar(99L);

        // Ejecutar petición DELETE y verificar error HTTP 500 (Internal Server Error)
        mockMvc.perform(delete("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE ACTUALIZACIÓN ================= */

    // Verifica actualización exitosa de usuario existente
    @Test
    public void actualizarUsuarioExistenteTest() throws Exception {
        Long id = 1L;

        // Configurar usuario existente usando SETTERS
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(id);
        usuarioExistente.setUsername("admin");
        usuarioExistente.setEmail("admin@duoc.cl");
        usuarioExistente.setRut("12345678-9");
        usuarioExistente.setPassword("password123");
        usuarioExistente.setRole("super_admin");
        usuarioExistente.setStatus("activo");
        usuarioExistente.setCreatedAt(LocalDateTime.now());
        usuarioExistente.setRegion("Metropolitana");
        usuarioExistente.setComuna("Santiago");
        usuarioExistente.setApellidos("Administrador Original");
        usuarioExistente.setFechaNacimiento("1985-05-15");
        usuarioExistente.setDireccion("Av. Original 123");
        
        // Configurar usuario actualizado usando SETTERS
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setUsername("adminActualizado");
        usuarioActualizado.setEmail("admin@duoc.cl");
        usuarioActualizado.setRut("12345678-9");
        usuarioActualizado.setPassword("nuevaPassword");
        usuarioActualizado.setRole("super_admin");
        usuarioActualizado.setStatus("activo");
        usuarioActualizado.setCreatedAt(LocalDateTime.now());
        usuarioActualizado.setRegion("Valparaíso");
        usuarioActualizado.setComuna("Viña del Mar");
        usuarioActualizado.setApellidos("Administrador Actualizado");
        usuarioActualizado.setFechaNacimiento("1985-05-15");
        usuarioActualizado.setDireccion("Av. Actualizada 456");

        // Configurar mocks del servicio para obtener y actualizar usuario
        when(usuarioService.obtenerPorID(id)).thenReturn(usuarioExistente);
        when(usuarioService.actualizar(eq(id), any(Usuario.class)))
                .thenReturn(usuarioActualizado);

        // Ejecutar petición PUT con datos actualizados y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk());
    }

    // Verifica manejo de actualización de usuario no existente
    @Test
    public void actualizarUsuarioNoExisteTest() throws Exception {
        Long id = 99L;

        // Configurar usuario que no existe usando SETTERS
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("noExiste");
        usuario.setEmail("noexiste@duoc.cl");
        usuario.setRut("12345678-9");
        usuario.setPassword("password123");
        usuario.setRole("vendedor");
        usuario.setStatus("activo");
        usuario.setCreatedAt(LocalDateTime.now());

        // Configurar mock del servicio para lanzar excepción al actualizar usuario inexistente
        when(usuarioService.actualizar(eq(id), any(Usuario.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado."));

        // Ejecutar petición PUT y verificar error HTTP 400 (Bad Request)
        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    // Verifica validación de email durante actualización
    @Test
    public void actualizarUsuarioEmailInvalidoTest() throws Exception {
        Long id = 1L;
        
        // Configurar usuario existente
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(id);
        usuarioExistente.setUsername("admin");
        usuarioExistente.setEmail("admin@duoc.cl");
        usuarioExistente.setRut("12345678-9");
        usuarioExistente.setPassword("password123");
        usuarioExistente.setRole("super_admin");
        usuarioExistente.setStatus("activo");
        usuarioExistente.setCreatedAt(LocalDateTime.now());
        
        // Configurar usuario actualizado con email inválido
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setUsername("adminActualizado");
        usuarioActualizado.setEmail("admin@yahoo.com"); // Email inválido
        usuarioActualizado.setRut("12345678-9");
        usuarioActualizado.setPassword("nuevaPassword");
        usuarioActualizado.setRole("super_admin");
        usuarioActualizado.setStatus("activo");
        usuarioActualizado.setCreatedAt(LocalDateTime.now());

        // Configurar mocks del servicio para obtener y lanzar excepción por email inválido
        when(usuarioService.obtenerPorID(id)).thenReturn(usuarioExistente);
        when(usuarioService.actualizar(eq(id), any(Usuario.class)))
                .thenThrow(new RuntimeException("El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com"));

        // Ejecutar petición PUT y verificar error HTTP 400 (Bad Request)
        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isBadRequest());
    }

    /* ================= PRUEBAS ADICIONALES PARA VALIDACIONES DE EMAIL ================= */

    // Verifica creación exitosa con email Gmail válido
    @Test
    public void crearUsuarioConEmailGmailValidoTest() throws Exception {
        // Configurar usuario con email Gmail válido usando SETTERS
        Usuario usuarioGmail = new Usuario();
        usuarioGmail.setUsername("usuarioGmail");
        usuarioGmail.setEmail("usuario@gmail.com"); // Email Gmail válido
        usuarioGmail.setRut("12345678-9");
        usuarioGmail.setPassword("password123");
        usuarioGmail.setRole("cliente");
        usuarioGmail.setStatus("activo");
        usuarioGmail.setRegion("Metropolitana");
        usuarioGmail.setComuna("Santiago");
        usuarioGmail.setApellidos("Usuario Gmail");
        usuarioGmail.setFechaNacimiento("1990-01-01");
        usuarioGmail.setDireccion("Calle Gmail 123");
        
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(4L);
        usuarioGuardado.setUsername("usuarioGmail");
        usuarioGuardado.setEmail("usuario@gmail.com");
        usuarioGuardado.setRut("12345678-9");
        usuarioGuardado.setPassword("passwordEncriptado");
        usuarioGuardado.setRole("cliente");
        usuarioGuardado.setStatus("activo");
        usuarioGuardado.setCreatedAt(LocalDateTime.now());
        usuarioGuardado.setRegion("Metropolitana");
        usuarioGuardado.setComuna("Santiago");
        usuarioGuardado.setApellidos("Usuario Gmail");
        usuarioGuardado.setFechaNacimiento("1990-01-01");
        usuarioGuardado.setDireccion("Calle Gmail 123");

        // Configurar mock del servicio para retornar usuario creado
        when(usuarioService.crear(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Ejecutar petición POST y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioGmail)))
                .andExpect(status().isOk());
    }

    // Verifica creación exitosa con email profesor Duoc válido
    @Test
    public void crearUsuarioConEmailProfesorValidoTest() throws Exception {
        // Configurar usuario con email profesor válido usando SETTERS
        Usuario usuarioProfesor = new Usuario();
        usuarioProfesor.setUsername("profesor");
        usuarioProfesor.setEmail("profesor@profesor.duoc.cl"); // Email profesor válido
        usuarioProfesor.setRut("12345678-9");
        usuarioProfesor.setPassword("password123");
        usuarioProfesor.setRole("administrador");
        usuarioProfesor.setStatus("activo");
        usuarioProfesor.setRegion("Metropolitana");
        usuarioProfesor.setComuna("Santiago");
        usuarioProfesor.setApellidos("Profesor Duoc");
        usuarioProfesor.setFechaNacimiento("1980-01-01");
        usuarioProfesor.setDireccion("Av. Profesor 456");
        
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(5L);
        usuarioGuardado.setUsername("profesor");
        usuarioGuardado.setEmail("profesor@profesor.duoc.cl");
        usuarioGuardado.setRut("12345678-9");
        usuarioGuardado.setPassword("passwordEncriptado");
        usuarioGuardado.setRole("administrador");
        usuarioGuardado.setStatus("activo");
        usuarioGuardado.setCreatedAt(LocalDateTime.now());
        usuarioGuardado.setRegion("Metropolitana");
        usuarioGuardado.setComuna("Santiago");
        usuarioGuardado.setApellidos("Profesor Duoc");
        usuarioGuardado.setFechaNacimiento("1980-01-01");
        usuarioGuardado.setDireccion("Av. Profesor 456");

        // Configurar mock del servicio para retornar usuario creado
        when(usuarioService.crear(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Ejecutar petición POST y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioProfesor)))
                .andExpect(status().isOk());
    }

    // Verifica actualización completa con modificación de todos los campos
    @Test
    public void actualizarUsuarioConNuevosCamposTest() throws Exception {
        Long id = 2L;

        // Configurar usuario existente
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(id);
        usuarioExistente.setUsername("vendedor1");
        usuarioExistente.setEmail("vendedor1@duoc.cl");
        usuarioExistente.setRut("98765432-1");
        usuarioExistente.setPassword("password456");
        usuarioExistente.setRole("vendedor");
        usuarioExistente.setStatus("activo");
        usuarioExistente.setCreatedAt(LocalDateTime.now());
        usuarioExistente.setRegion("Valparaíso");
        usuarioExistente.setComuna("Viña del Mar");
        usuarioExistente.setApellidos("Vendedor Original");
        usuarioExistente.setFechaNacimiento("1990-08-20");
        usuarioExistente.setDireccion("Calle Original 456");
        
        // Configurar usuario actualizado con todos los campos nuevos modificados
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setUsername("vendedorActualizado");
        usuarioActualizado.setEmail("vendedorActualizado@duoc.cl");
        usuarioActualizado.setRut("98765432-1");
        usuarioActualizado.setPassword("password456");
        usuarioActualizado.setRole("vendedor");
        usuarioActualizado.setStatus("inactivo");
        usuarioActualizado.setCreatedAt(LocalDateTime.now());
        usuarioActualizado.setRegion("Metropolitana");
        usuarioActualizado.setComuna("Santiago");
        usuarioActualizado.setApellidos("Vendedor Modificado");
        usuarioActualizado.setFechaNacimiento("1992-10-15");
        usuarioActualizado.setDireccion("Av. Modificada 789");

        // Configurar mocks del servicio para obtener y actualizar usuario
        when(usuarioService.obtenerPorID(id)).thenReturn(usuarioExistente);
        when(usuarioService.actualizar(eq(id), any(Usuario.class)))
                .thenReturn(usuarioActualizado);

        // Ejecutar petición PUT y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk());
    }
}