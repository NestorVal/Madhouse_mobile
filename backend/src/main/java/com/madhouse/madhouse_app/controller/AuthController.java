package com.madhouse.madhouse_app.controller;

import com.madhouse.madhouse_app.model.Usuario;
import com.madhouse.madhouse_app.repo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para devolver códigos HTTP (201, 401, etc.)
import org.springframework.http.ResponseEntity; // Para enviar respuestas completas con código + JSON
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar/validar contraseñas
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Controlador para registro y login de usuarios (sin requerir autenticación previa)

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para registrar un nuevo usuario
    // Valida que el correo no exista, encripta la contraseña, asigna ROLE_CLIENTE por defecto
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Verificar si el correo ya existe
            Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo());
            if (usuarioExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error: El correo ya está registrado.");
            }

            // Encriptar la contraseña
            String hashPassword = passwordEncoder.encode(usuario.getContrasena());
            usuario.setContrasena(hashPassword);

            // Asignar rol por defecto
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("ROLE_CLIENTE");
            }

            // Guardar en BD
            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar: " + e.getMessage());
        }
    }

    // Endpoint para login: busca usuario por correo y valida contraseña
    // Devuelve datos del usuario (sin contraseña) si es correcto
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario datosLogin) {
        Map<String, Object> response = new HashMap<>();

        // Buscar usuario por correo
        Optional<Usuario> usuarioFisico = usuarioRepository.findByCorreo(datosLogin.getCorreo());

        if (usuarioFisico.isPresent()) {
            Usuario usuario = usuarioFisico.get();

            // Validar contraseña
            boolean contrasenaCoincide = passwordEncoder.matches(
                    datosLogin.getContrasena(),
                    usuario.getContrasena()
            );

            if (contrasenaCoincide) {
                // Login exitoso: devolver datos del usuario
                response.put("status", "success");
                response.put("mensaje", "Autenticación satisfactoria");
                response.put("idUsuario", usuario.getIdUsuario());
                response.put("nombre", usuario.getNombre());
                response.put("apellido", usuario.getApellido());
                response.put("correo", usuario.getCorreo());
                response.put("foto", usuario.getFoto());
                response.put("rol", usuario.getRol());
                response.put("telefono", usuario.getTelefono());
                response.put("direccion", usuario.getDireccion());
                response.put("fechaNacimiento", usuario.getFechaNacimiento());
                response.put("especialidad", usuario.getEspecialidad());
                response.put("biografia", usuario.getBiografia());
                
                return ResponseEntity.ok(response);
            } else {
                // Contraseña incorrecta
                response.put("status", "error");
                response.put("mensaje", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            // Correo no encontrado
            response.put("status", "error");
            response.put("mensaje", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}