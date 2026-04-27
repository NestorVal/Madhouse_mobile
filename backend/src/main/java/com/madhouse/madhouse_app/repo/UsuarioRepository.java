package com.madhouse.madhouse_app.repo;
import com.madhouse.madhouse_app.model.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// Un Repository accede a la tabla "usuario" en MySQL
// Spring Data JPA genera automáticamente las consultas SQL basadas en el nombre del método

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Busca un usuario por su correo (el correo es ÚNICO, no hay dos iguales)
    // Devuelve Optional<Usuario> porque el usuario podría no existir
    // Casos de uso:
    // 1. REGISTRO: Verificar si el correo ya está registrado
    // 2. LOGIN: Buscar el usuario por correo para validar contraseña
    // SQL generado: SELECT * FROM usuario WHERE correo = ?
    Optional<Usuario> findByCorreo(String correo);

    // Busca un usuario por correo Y contraseña (NO se usa actualmente, dejamos para compatibilidad)
    // Es mejor usar findByCorreo() y luego validar contraseña con passwordEncoder
    Usuario findByCorreoAndContrasena(String correo, String contrasena);

    // Busca todos los usuarios con un rol específico
    // Caso de uso: Obtener lista de barberos para mostrar en la página "Barberos"
    // SQL generado: SELECT * FROM usuario WHERE rol = ?
    // Parámetro: "ROLE_CLIENTE" o "ROLE_BARBERO"
    List<Usuario> findByRol(String rol);
}
