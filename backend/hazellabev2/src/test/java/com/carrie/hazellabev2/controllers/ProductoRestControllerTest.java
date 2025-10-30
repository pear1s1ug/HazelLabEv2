package com.carrie.hazellabev2.controllers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
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

import com.carrie.hazellabev2.entities.Categoria;
import com.carrie.hazellabev2.entities.Producto;
import com.carrie.hazellabev2.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

/* ================= Pruebas de Integración para Controlador de Productos ================= */

/**
 * Pruebas de integración para ProductoRestController.
 * Evalúa endpoints HTTP reales para operaciones CRUD de productos.
 * Utiliza Spring Boot Test con MockMvc para testing de capa web sin servidor completo.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductoRestControllerTest {

    // Cliente MVC simulado para realizar peticiones HTTP a endpoints de productos
    @Autowired
    private MockMvc mockMvc;

    // Utilidad para serializar/deserializar objetos Java a JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Simula el servicio de productos para aislar la capa de controlador
    @MockBean
    private ProductoService productoService;

    /* ================= PRUEBAS PARA ENDPOINT DE LISTADO ================= */

    // Verifica que el endpoint GET /api/productos retorna lista completa de productos
    @Test
    public void listarProductosTest() throws Exception {
        // Configurar categoría y productos de prueba
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto prod1 = new Producto(1L, "Ácido Clorhídrico", "Lote001", "Solución corrosiva", "HCL001",
                Date.valueOf("2026-12-31"), Date.valueOf("2024-01-01"),
                5000, 20, 5, "Proveedor Químico S.A.", cat, "imagen1.jpg",
                true, LocalDateTime.now(), true);
        Producto prod2 = new Producto(2L, "Etanol", "Lote002", "Alcohol 96%", "ETH002",
                Date.valueOf("2027-06-30"), Date.valueOf("2024-02-01"),
                3000, 40, 10, "Distribuidora Alcoholes Ltda.", cat, "imagen2.jpg",
                true, LocalDateTime.now(), false);

        // Configurar mock del servicio para retornar lista de productos
        when(productoService.listarTodo()).thenReturn(Arrays.asList(prod1, prod2));

        // Ejecutar petición GET y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE OBTENCIÓN POR ID ================= */

    // Verifica obtención exitosa de producto específico por ID
    @Test
    public void obtenerProductoPorIdTest() {
        // Configurar producto de prueba con categoría
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto producto = new Producto(1L, "Ácido Clorhídrico", "Lote001",
                "Solución corrosiva", "HCL001",
                Date.valueOf("2026-12-31"), Date.valueOf("2024-01-01"),
                5000, 20, 5, "Proveedor Químico S.A.", cat,
                "imagen1.jpg", true, LocalDateTime.now(), true);

        try {
            // Configurar mock del servicio para retornar producto específico
            when(productoService.obtenerPorID(1L)).thenReturn(producto);
            
            // Ejecutar petición GET con ID y verificar respuesta HTTP 200 (OK)
            mockMvc.perform(get("/api/productos/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception ex) {
            fail("Error en obtenerProductoPorIdTest: " + ex.getMessage());
        }
    }

    // Verifica manejo adecuado cuando se busca producto con ID inexistente
    @Test
    public void productoNoExisteTest() throws Exception {
        // Configurar mock del servicio para lanzar excepción de producto no encontrado
        when(productoService.obtenerPorID(99L))
                .thenThrow(new RuntimeException("Producto no encontrado."));

        // Ejecutar petición GET y verificar que retorna error HTTP 500 (Internal Server Error)
        mockMvc.perform(get("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE CREACIÓN ================= */

    // Verifica creación exitosa de nuevo producto con datos válidos
    @Test
    public void crearProductoTest() throws Exception {
        // Configurar categoría y producto nuevo (sin ID)
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto nuevo = new Producto(null, "Nuevo Reactivo", "Lote003", "Reactivo nuevo", "NEW003",
                Date.valueOf("2028-01-01"), Date.valueOf("2025-01-01"),
                7000, 10, 3, "Nuevo Proveedor S.A.", cat, "imagen3.jpg",
                true, null, true);
        
        // Configurar producto guardado (con ID generado)
        Producto guardado = new Producto(3L, "Nuevo Reactivo", "Lote003", "Reactivo nuevo", "NEW003",
                Date.valueOf("2028-01-01"), Date.valueOf("2025-01-01"),
                7000, 10, 3, "Nuevo Proveedor S.A.", cat, "imagen3.jpg",
                true, LocalDateTime.now(), true);

        // Configurar mock del servicio para retornar producto creado
        when(productoService.crear(org.mockito.ArgumentMatchers.any(Producto.class))).thenReturn(guardado);

        // Ejecutar petición POST con datos JSON y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE ACTUALIZACIÓN ================= */

    // Verifica actualización exitosa de producto existente
    @Test
    public void actualizarProductoExistenteTest() throws Exception {
        // Configurar categoría y producto actualizado
        Categoria cat = new Categoria(1L, "Químicos", null);
        Long id = 1L;
        Producto actualizado = new Producto(id, "Etanol Puro", "Lote002",
                "Alcohol refinado", "ETH002",
                Date.valueOf("2027-06-30"), Date.valueOf("2024-02-01"),
                3200, 35, 8, "Distribuidora Alcoholes Premium Ltda.", cat,
                "imagen2.jpg", true, LocalDateTime.now(), true);

        // Configurar mock del servicio para retornar producto actualizado
        when(productoService.actualizar(org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any(Producto.class))).thenReturn(actualizado);

        // Ejecutar petición PUT con datos actualizados y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(put("/api/productos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk());
    }

    // Verifica manejo de actualización de producto no existente
    @Test
    public void actualizarProductoNoExisteTest() throws Exception {
        // Configurar categoría y producto que no existe
        Categoria cat = new Categoria(1L, "Químicos", null);
        Long id = 99L;
        Producto producto = new Producto(id, "No Existe", "Lote999", "Producto falso", "ERR999",
                Date.valueOf("2029-01-01"), Date.valueOf("2025-01-01"),
                1000, 0, 0, "Proveedor Inexistente", cat,
                "img.jpg", true, LocalDateTime.now(), false);

        // Configurar mock del servicio para lanzar excepción al actualizar producto inexistente
        when(productoService.actualizar(org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any(Producto.class)))
                .thenThrow(new RuntimeException("Producto no encontrado."));

        // Ejecutar petición PUT y verificar error HTTP 500 (Internal Server Error)
        mockMvc.perform(put("/api/productos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isInternalServerError());
    }

    /* ================= PRUEBAS PARA ENDPOINT DE ELIMINACIÓN ================= */

    // Verifica eliminación exitosa de producto existente
    @Test
    public void eliminarProductoTest() throws Exception {
        // Configurar mock del servicio para verificar que producto existe
        when(productoService.obtenerPorID(1L))
                .thenReturn(new Producto(1L, "Ácido Clorhídrico", "Lote001", "Solución corrosiva", "HCL001",
                        Date.valueOf("2026-12-31"), Date.valueOf("2024-01-01"),
                        5000, 20, 5, "Proveedor Químico S.A.", null,
                        "imagen1.jpg", true, LocalDateTime.now(), true));

        // Ejecutar petición DELETE y verificar respuesta HTTP 204 (No Content)
        mockMvc.perform(delete("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Verifica manejo de eliminación de producto no existente
    @Test
    public void eliminarProductoNoExisteTest() throws Exception {
        // Configurar mock del servicio para lanzar excepción al eliminar producto inexistente
        doThrow(new RuntimeException("Producto no encontrado."))
                .when(productoService).eliminar(99L);

        // Ejecutar petición DELETE y verificar error HTTP 500 (Internal Server Error)
        mockMvc.perform(delete("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /* ================= PRUEBAS PARA ENDPOINTS ESPECIALIZADOS ================= */

    // Verifica obtención de productos marcados como destacados
    @Test
    public void listarProductosDestacadosTest() throws Exception {
        // Configurar categoría y producto destacado
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto prod1 = new Producto(1L, "Reactivo Premium", "Lote005", "Alta pureza", "PREM001",
                Date.valueOf("2027-12-31"), Date.valueOf("2024-03-01"),
                12000, 15, 3, "Proveedor Premium S.A.", cat,
                "imagen5.jpg", true, LocalDateTime.now(), true);
        
        // Configurar mock del servicio para retornar productos destacados
        when(productoService.listarDestacados()).thenReturn(List.of(prod1));

        // Ejecutar petición GET a endpoint específico de destacados y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(get("/api/productos/destacados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Verifica desactivación de producto mediante endpoint PATCH
    @Test
    public void desactivarProductoTest() throws Exception {
        Long id = 1L;
        // Configurar categoría y producto desactivado
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto desactivado = new Producto(id, "Etanol", "Lote002", "Alcohol", "ETH002",
                Date.valueOf("2027-06-30"), Date.valueOf("2024-02-01"),
                3000, 40, 10, "Distribuidora Alcoholes Ltda.", cat,
                "imagen2.jpg", false, LocalDateTime.now(), false);

        // Configurar mock del servicio para retornar producto desactivado
        when(productoService.desactivar(id)).thenReturn(desactivado);

        // Ejecutar petición PATCH a endpoint de desactivación y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(patch("/api/productos/{id}/desactivar", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Verifica actualización de imagen de producto mediante endpoint especializado
    @Test
    public void uploadImageProductoTest() throws Exception {
        Long id = 1L;
        // Configurar categoría y producto con nueva imagen
        Categoria cat = new Categoria(1L, "Químicos", null);
        Producto producto = new Producto(id, "Etanol", "Lote002", "Alcohol", "ETH002",
                Date.valueOf("2027-06-30"), Date.valueOf("2024-02-01"),
                3000, 40, 10, "Distribuidora Alcoholes Ltda.", cat,
                "nueva-imagen.jpg", true, LocalDateTime.now(), false);

        // Configurar mocks del servicio para obtener y actualizar producto
        when(productoService.obtenerPorID(id)).thenReturn(producto);
        when(productoService.actualizar(org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any(Producto.class))).thenReturn(producto);

        // Ejecutar petición POST a endpoint de carga de imagen y verificar respuesta HTTP 200 (OK)
        mockMvc.perform(post("/api/productos/{id}/upload-image", id)
                .param("imageUrl", "http://imagenes.com/img.jpg")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}