package com.carrie.hazellabev2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    /* ================= Configuración de CORS (Cross-Origin Resource Sharing) ================= */
    // CORS es un mecanismo que permite que una aplicación web en un dominio acceda a recursos de otro dominio (Intercambio de recursos de origen cruzado)

    // Método que configura las políticas CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // Dominio permitido para hacer solicitudes: aplicación frontend en desarrollo
                        .allowedOrigins("http://localhost:5173")
                        
                        // Métodos HTTP permitidos para las solicitudes cruzadas
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        
                        // Todos los tipos de encabezados HTTP permitidos en las solicitudes (metadata)
                        .allowedHeaders("*")
                        
                        // Permite el envío de credenciales (cookies, autenticación) en solicitudes cruzadas
                        .allowCredentials(true);
            }
        };
    }
}