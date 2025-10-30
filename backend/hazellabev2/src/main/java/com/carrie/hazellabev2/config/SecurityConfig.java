package com.carrie.hazellabev2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /* ================= Configuración global de seguridad ================= */

    // Método interceptor de peticiones. Levanta protecciones y seguridad para facilitar las pruebas. DESACTIVAR en producción.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar protección CSRF para simplificar el desarrollo (Cross-Site Request Forgery, tipo de ataque malicioso web.)
            .csrf(csrf -> csrf.disable())
            
            // Configuración de autorización de solicitudes HTTP
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a la documentación de Swagger/OpenAPI para que desarrolladores puedan ver los endpoints sin autenticación
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Permite acceso público a todos los endpoints de la API. 
                .requestMatchers("/api/**").permitAll()
                
                // Permite acceso público a cualquier otra solicitud.
                .anyRequest().permitAll()
            )
            
            // Deshabilitar formulario de login por defecto de Spring Security
            .formLogin(login -> login.disable())
            
            // Deshabilitar autenticación HTTP Basic
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // Método que configura BCrypt como el codificador de contraseñas. BCrypt es un algoritmo seguro que incluye salting automático.
    // SALTING: Valor aleatorio que hace que incluso dos contraseñas idénticas tengan "hashes" (contraseñas encriptadas) completamente diferentes en la base de datos.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}