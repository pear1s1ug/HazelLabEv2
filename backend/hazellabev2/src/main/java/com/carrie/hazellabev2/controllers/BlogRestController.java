package com.carrie.hazellabev2.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrie.hazellabev2.entities.Blog;
import com.carrie.hazellabev2.services.BlogService;

/* ================= Controlador REST para Gestión de Blogs (Regla de negocio) ================= */

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/blogs")

public class BlogRestController {
    /* Limitaciones con posts largos debido a restricciones del tipo String estándar en el campo 'body'
    
    SOLUCIÓN RECOMENDADA para implementación futura:
    1. En la entidad Blog: usar @Lob sobre el campo contenido
    2. En base de datos: configurar columna como TEXT/LONGTEXT
    3. En aplicación: validar límites según requerimientos de negocio
    
    EJEMPLO:
    @Lob
    @Column(columnDefinition = "TEXT")
    private String contenido;
    /* =============================================================== */
    
    // Inyección automática del servicio de blogs - Spring proporciona la implementación
    @Autowired
    private BlogService blogService;
    
    /* ================= Endpoints CRUD (Crear, Leer, Actualizar, Eliminar) ================= */
    
    // Endpoint para crear un nuevo blog. POST /api/blogs - Recibe los datos del blog en el cuerpo de la solicitud
    @PostMapping
    public ResponseEntity<Blog> crearBlog(@RequestBody Blog blog) {
        Blog nuevoBlog = blogService.crear(blog);
        // Retorna código HTTP 200 (OK) con el blog creado
        return ResponseEntity.ok(nuevoBlog);
    }

    // Endpoint para obtener un blog específico por su ID. GET /api/blogs/{id} - Extrae el ID de la ruta URL
    @GetMapping("/{id}")
    public ResponseEntity<Blog> obtenerBlogPorId(@PathVariable Long id) {
        Blog blog = blogService.obtenerPorID(id);
        return ResponseEntity.ok(blog);
    }
  
    // Endpoint para actualizar un blog existente. PUT /api/blogs/{id} - Combina ID de la ruta con datos actualizados del cuerpo
    @PutMapping("/{id}")
    public ResponseEntity<Blog> actualizarBlog(@PathVariable Long id, @RequestBody Blog blogActualizado) {
        Blog blog = blogService.actualizar(id, blogActualizado);
        return ResponseEntity.ok(blog);
    }

    // Endpoint para listar todos los blogs. GET /api/blogs - No requiere parámetros, devuelve lista completa
    @GetMapping
    public ResponseEntity<List<Blog>> listarBlogs() {
        List<Blog> blogs = blogService.listarTodo();
        return ResponseEntity.ok(blogs);
    }

    // Endpoint para eliminar un blog por su ID. DELETE /api/blogs/{id} - Retorna código 204 (No Content) indicando éxito sin datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBlog(@PathVariable Long id) {
        blogService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}