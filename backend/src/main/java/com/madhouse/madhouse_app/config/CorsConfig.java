package com.madhouse.madhouse_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Marca esta clase como una clase de configuración de Spring
public class CorsConfig implements WebMvcConfigurer { // Implementa WebMvcConfigurer para configurar CORS
    @Override // Sobrescribe el método addCorsMappings para configurar las reglas de CORS
    public void addCorsMappings(CorsRegistry registry) { // Configura las reglas de CORS para la aplicación
        registry.addMapping("/**") // Permite todas las rutas de la API
                .allowedOrigins("http://localhost:3000") // Permite a React conectarse
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite los métodos HTTP comunes
                .allowedHeaders("*") // Permite todos los encabezados
                .allowCredentials(true); // Permite el envío de cookies y credenciales en las solicitudes CORS
    }
}