package com.carrie.hazellabev2.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.carrie.hazellabev2.entities.Categoria;
import com.carrie.hazellabev2.entities.Producto;
import com.carrie.hazellabev2.repositories.ProductoRepository;

/* ================= Pruebas Unitarias para Servicio de Productos ================= */

/**
 * Pruebas unitarias completas para UsuarioServiceImpl.
 * No requiere base de datos ni contexto de Spring Boot.
 * Utiliza Mockito para simular dependencias y aislar el código bajo prueba.
 */
public class ProductoServiceImplTest {
    // Simula el repositorio de productos para evitar acceso real a base de datos
    @Mock
    private ProductoRepository repository;

    // Inyecta los mocks en el servicio real que se está probando
    @InjectMocks
    private ProductoServiceImpl service;

    // Productos de prueba reutilizables para múltiples casos de prueba
    private Producto prod1;
    private Producto prod2;
    private Categoria categoria;

    /* ================= Configuración Inicial ================= */
    
    // Configura el entorno de prueba antes de cada método de prueba
    @BeforeEach
    void setUp() {
        // Inicializa los mocks de Mockito
        MockitoAnnotations.openMocks(this);

        // Crea categoría de prueba para asociar a los productos
        categoria = new Categoria(1L, "Químicos", null);

        // Crea primer producto de prueba: producto químico con stock normal
        prod1 = new Producto(1L, "Ácido Clorhídrico", "Lote001", "Solución corrosiva", "HCL001",
                Date.valueOf("2026-12-31"), Date.valueOf("2024-01-01"),
                5000, 20, 5, "Proveedor Químico S.A.", categoria, "imagen1.jpg",
                true, LocalDateTime.now(), false);

        // Crea segundo producto de prueba: equipo con stock bajo y destacado
        prod2 = new Producto(2L, "Centrífuga", "Lote002", "Equipo de laboratorio", "EQP001",
                Date.valueOf("2027-05-20"), Date.valueOf("2024-02-10"),
                20000, 3, 5, "Laboratorios Chile", categoria, "imagen2.jpg",
                true, LocalDateTime.now(), true);
    }

    /* ================= PRUEBAS PARA CREACIÓN DE PRODUCTOS ================= */

    // Verifica que un producto se crea exitosamente con todos los datos requeridos
    @Test
    void crearProductoTest() {
        // Configura repositorio simulado para retornar el producto guardado
        when(repository.save(prod1)).thenReturn(prod1);

        // Ejecuta el método bajo prueba
        Producto creado = service.crear(prod1);

        // Verificaciones: producto no nulo, nombre correcto, repositorio llamado una vez
        assertNotNull(creado);
        assertEquals("Ácido Clorhídrico", creado.getName());
        verify(repository, times(1)).save(prod1);
    }

    /* ================= PRUEBAS PARA OBTENCIÓN POR ID ================= */

    // Verifica recuperación exitosa de producto existente por su ID
    @Test
    void obtenerPorIdExistenteTest() {
        // Configura repositorio para retornar producto cuando se busca por ID 1
        when(repository.findById(1L)).thenReturn(Optional.of(prod1));

        // Ejecuta la búsqueda por ID
        Producto result = service.obtenerPorID(1L);

        // Verifica que el producto retornado tiene el nombre esperado
        assertEquals("Ácido Clorhídrico", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    // Verifica manejo de caso cuando se busca un producto con ID inexistente
    @Test
    void obtenerPorIdNoExistenteTest() {
        // Configura repositorio para retornar vacío cuando se busca ID 99
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Ejecuta y verifica que lanza excepción con mensaje específico
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorID(99L));
        assertEquals("Producto no encontrado.", ex.getMessage());
    }

    /* ================= PRUEBAS PARA ACTUALIZACIÓN DE PRODUCTOS ================= */

    // Verifica actualización exitosa de producto existente con nuevos datos
    @Test
    void actualizarProductoExistenteTest() {
        // Configura mocks: producto existe y repositorio guarda cambios
        when(repository.findById(1L)).thenReturn(Optional.of(prod1));
        when(repository.save(any(Producto.class))).thenReturn(prod1);

        // Modifica el nombre del producto
        prod1.setName("Ácido Sulfúrico");
        
        // Ejecuta actualización
        Producto actualizado = service.actualizar(1L, prod1);

        // Verifica que el nombre se actualizó correctamente
        assertEquals("Ácido Sulfúrico", actualizado.getName());
        verify(repository, times(1)).save(prod1);
    }

    /* ================= PRUEBAS PARA ELIMINACIÓN DE PRODUCTOS ================= */

    // Verifica eliminación exitosa de producto existente
    @Test
    void eliminarProductoExistenteTest() {
        // Configura repositorio para confirmar que producto existe
        when(repository.existsById(1L)).thenReturn(true);

        // Ejecuta eliminación
        service.eliminar(1L);

        // Verifica que se llamó al método de eliminación con el ID correcto
        verify(repository, times(1)).deleteById(1L);
    }

    // Verifica manejo de eliminación de producto no existente
    @Test
    void eliminarProductoNoExistenteTest() {
        // Configura repositorio para indicar que producto no existe
        when(repository.existsById(99L)).thenReturn(false);

        // Verifica que lanza excepción con mensaje específico al intentar eliminar producto no existente
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminar(99L));
        assertEquals("Producto no encontrado.", ex.getMessage());
    }

    /* ================= PRUEBAS PARA GESTIÓN DE ESTADO ================= */

    // Verifica desactivación exitosa de producto (cambio de estado activo a inactivo)
    @Test
    void desactivarProductoTest() {
        // Asegura que el producto esté activo inicialmente
        prod1.setActiveStatus(true);
        when(repository.findById(1L)).thenReturn(Optional.of(prod1));
        when(repository.save(any(Producto.class))).thenReturn(prod1);

        // Ejecuta desactivación
        Producto result = service.desactivar(1L);

        // Verifica que el producto quedó inactivo
        assertFalse(result.getActiveStatus());
        verify(repository, times(1)).save(prod1);
    }

    // Verifica actualización exitosa de imagen de producto
    @Test
    void actualizarImagenTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(prod1));
        when(repository.save(any(Producto.class))).thenReturn(prod1);

        // Ejecuta actualización de imagen con nueva URL
        Producto result = service.actualizarImagen(1L, "http://imagenes.com/nueva.jpg");

        // Verifica que la URL de la imagen se actualizó correctamente
        assertEquals("http://imagenes.com/nueva.jpg", result.getImage());
        verify(repository, times(1)).save(prod1);
    }

    /* ================= PRUEBAS PARA LISTADOS Y CONSULTAS ================= */

    // Verifica obtención de lista completa de todos los productos
    @Test
    void listarTodoTest() {
        // Configura repositorio para retornar lista con ambos productos
        when(repository.findAll()).thenReturn(List.of(prod1, prod2));

        // Ejecuta listado completo
        List<Producto> lista = service.listarTodo();

        // Verifica que retorna exactamente 2 productos
        assertEquals(2, lista.size());
        verify(repository, times(1)).findAll();
    }

    // Verifica obtención de productos marcados como destacados
    @Test
    void listarDestacadosTest() {
        // Configura repositorio para retornar solo productos destacados
        when(repository.findByDestacadoTrue()).thenReturn(List.of(prod2));

        // Ejecuta búsqueda de destacados
        List<Producto> destacados = service.listarDestacados();

        // Verifica que retorna un producto y que está marcado como destacado
        assertEquals(1, destacados.size());
        assertTrue(destacados.get(0).getDestacado());
        verify(repository, times(1)).findByDestacadoTrue();
    }

    /* ================= PRUEBAS PARA BÚSQUEDAS Y FILTROS ================= */

    // Verifica búsqueda de productos por fragmento de nombre (case-insensitive)
    @Test
    void buscarPorNombreTest() {
        // Configura repositorio para retornar productos que contengan "ácido" en el nombre
        when(repository.findByNameContainingIgnoreCase("ácido")).thenReturn(List.of(prod1));

        // Ejecuta búsqueda por nombre
        List<Producto> resultado = service.buscarPorNombre("ácido");

        // Verifica que retorna exactamente un producto
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByNameContainingIgnoreCase("ácido");
    }

    // Verifica filtrado de productos por categoría específica
    @Test
    void buscarPorCategoriaTest() {
        // Configura repositorio para retornar productos de la categoría 1
        when(repository.findByCategoryId(1L)).thenReturn(List.of(prod1, prod2));

        // Ejecuta búsqueda por categoría
        List<Producto> resultado = service.buscarPorCategoria(1L);

        // Verifica que retorna exactamente 2 productos
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findByCategoryId(1L);
    }

    // Verifica detección de productos con stock bajo (menor al stock mínimo)
    @Test
    void buscarProductosStockBajoTest() {
        // Configura repositorio para retornar productos con stock menor a 5
        List<Producto> productosStockBajo = List.of(prod2); // Centrífuga tiene stock = 3
        when(repository.findByStockLessThan(5)).thenReturn(productosStockBajo);

        // Ejecuta búsqueda de stock bajo
        List<Producto> response = service.buscarProductosStockBajo();

        // Verificaciones: un producto retornado, nombre correcto, stock bajo confirmado
        assertEquals(1, response.size());
        assertEquals("Centrífuga", response.get(0).getName());
        assertTrue(response.get(0).getStock() < 5);
        verify(repository, times(1)).findByStockLessThan(5);
    }

    // Verifica filtrado de productos por estado activo/inactivo
    @Test
    void buscarPorEstadoTest() {
        // Configura repositorio para retornar productos activos
        when(repository.findByActiveStatus(true)).thenReturn(List.of(prod1, prod2));

        // Ejecuta búsqueda por estado activo
        List<Producto> activos = service.buscarPorEstado(true);

        // Verifica que retorna 2 productos activos
        assertEquals(2, activos.size());
        verify(repository, times(1)).findByActiveStatus(true);
    }

    // Verifica búsqueda combinada por nombre y categoría
    @Test
    void buscarPorNombreYCategoriaTest() {
        // Configura repositorio para búsqueda combinada
        when(repository.findByNameContainingIgnoreCaseAndCategoryId("ácido", 1L))
                .thenReturn(List.of(prod1));

        // Ejecuta búsqueda combinada
        List<Producto> resultado = service.buscarPorNombreYCategoria("ácido", 1L);

        // Verifica que retorna un producto que cumple ambos criterios
        assertEquals(1, resultado.size());
        verify(repository, times(1))
                .findByNameContainingIgnoreCaseAndCategoryId("ácido", 1L);
    }

    // Verifica búsqueda combinada por nombre y estado
    @Test
    void buscarPorNombreYEstadoTest() {
        // Configura repositorio para búsqueda combinada por nombre y estado
        when(repository.findByNameContainingIgnoreCaseAndActiveStatus("centrífuga", true))
                .thenReturn(List.of(prod2));

        // Ejecuta búsqueda combinada
        List<Producto> resultado = service.buscarPorNombreYEstado("centrífuga", true);

        // Verifica que retorna un producto que cumple ambos criterios
        assertEquals(1, resultado.size());
        verify(repository, times(1))
                .findByNameContainingIgnoreCaseAndActiveStatus("centrífuga", true);
    }
}