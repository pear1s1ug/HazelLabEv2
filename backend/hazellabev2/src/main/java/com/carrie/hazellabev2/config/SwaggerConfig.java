package com.carrie.hazellabev2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    /* ================= Configuración de Documentación API ================= */
    
    // Método que define la configuración de Swagger/OpenAPI (documentación interactiva para APIs REST)
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        // Título de la documentación API que aparecerá en la interfaz web
                        .title("HazelLab API - Sistema de Gestión")
                        
                        // Versión actual de la API, importante para control de cambios
                        .version("1.0")
                        
                        // Descripción general de los propósitos y funcionalidades de la API
                        .description("API REST para el sistema de gestión de productos de laboratorio HazelLab")
                        
                        // Información de contacto del equipo de desarrollo. Útil para que los usuarios sepan a quién contactar por soporte
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@hazellab.com")
                                .url("http://hazellab.com"))
                        
                        // Licencia bajo la cual se distribuye la API. Define los términos de uso y distribución del software
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}