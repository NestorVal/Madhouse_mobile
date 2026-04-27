package com.madhouse.madhouse_app.controller;

import com.madhouse.madhouse_app.model.Usuario;
import com.madhouse.madhouse_app.repo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para devolver códigos HTTP (200, 404, etc.)
import org.springframework.http.ResponseEntity; // Para enviar respuestas completas con código + JSON
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar la nueva contraseña
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // Para crear JSON de respuesta
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Controlador para usuarios autenticados: perfil, edición, cambio de contraseña

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener datos del perfil de un usuario por su ID
    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // Actualizar información del perfil (nombre, teléfono, dirección, etc.)
    // NO actualiza correo, contraseña ni rol (tienen endpoints separados)
    // PUT /api/usuarios/actualizar/{id}
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Integer id, 
            @RequestBody Usuario datosActualizados) {
        Optional<Usuario> usuarioFisico = usuarioRepository.findById(id);

        if (usuarioFisico.isPresent()) {
            Usuario usuario = usuarioFisico.get();
            
            // Actualizar solo campos permitidos
            usuario.setNombre(datosActualizados.getNombre());
            usuario.setApellido(datosActualizados.getApellido());
            usuario.setTelefono(datosActualizados.getTelefono());
            usuario.setFechaNacimiento(datosActualizados.getFechaNacimiento());
            usuario.setDireccion(datosActualizados.getDireccion());
            
            if (datosActualizados.getEspecialidad() != null) {
                usuario.setEspecialidad(datosActualizados.getEspecialidad());
            }
            
            if (datosActualizados.getFoto() != null) {
                usuario.setFoto(datosActualizados.getFoto());
            }
            
            if (datosActualizados.getBiografia() != null) {
                usuario.setBiografia(datosActualizados.getBiografia());
            }

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuarioGuardado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // Cambiar la contraseña: valida la actual y guarda la nueva encriptada
    // PUT /api/usuarios/cambiar-password/{id}
    @PutMapping("/cambiar-password/{id}")
    public ResponseEntity<?> cambiarContrasena(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> passwords) {
        Optional<Usuario> usuarioFisico = usuarioRepository.findById(id);

        if (usuarioFisico.isPresent()) {
            Usuario usuario = usuarioFisico.get();
            
            String passwordActualRecibida = passwords.get("actual");
            String passwordNueva = passwords.get("nueva");

            if (passwordEncoder.matches(passwordActualRecibida, usuario.getContrasena())) {
                usuario.setContrasena(passwordEncoder.encode(passwordNueva));
                usuarioRepository.save(usuario);
                
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Contraseña actualizada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("La contraseña actual es incorrecta");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // Listar todos los usuarios (generalmente solo para administrador)
    // GET /api/usuarios
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Listar todos los barberos (para mostrar en página de Barberos y paso 2 de reservas)
    // GET /api/usuarios/barberos
    @GetMapping("/barberos")
    public List<Usuario> obtenerBarberos() {
        return usuarioRepository.findByRol("ROLE_BARBERO");
    }
}