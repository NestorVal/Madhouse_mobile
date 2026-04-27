package com.madhouse.madhouse_app.controller;

import com.madhouse.madhouse_app.dto.ReservaRequest;
import com.madhouse.madhouse_app.model.Reserva;
import com.madhouse.madhouse_app.model.ReservaServicio;
import com.madhouse.madhouse_app.model.Servicio;
import com.madhouse.madhouse_app.model.Usuario; // Importante
import com.madhouse.madhouse_app.repo.ReservaRepository;
import com.madhouse.madhouse_app.repo.ReservaServicioRepository;
import com.madhouse.madhouse_app.repo.ServicioRepository;
import com.madhouse.madhouse_app.repo.UsuarioRepository; // Importante

import java.util.List;
import java.util.Map; // NUEVO: Importado para manejar el JSON del estado
import java.util.Optional; // NUEVO: Importado para validar si la reserva existe

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // NUEVO: Para enviar códigos de estado HTTP
import org.springframework.http.ResponseEntity; // NUEVO: Para enviar respuestas HTTP completas
import org.springframework.web.bind.annotation.*;

// Controlador para gestionar reservas (crear, listar, actualizar estado)

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaServicioRepository reservaServicioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear una nueva reserva con sus servicios asociados
    // React envía: ReservaRequest { reserva, idServicios[] }
    // Se buscan cliente y barbero completos en BD, se guarda reserva y detalles de servicios
    @PostMapping("/crear")
    public Reserva crearReservaCompleta(@RequestBody ReservaRequest request) {
        
        Reserva reservaParaGuardar = request.getReserva();

        // Buscar cliente y barbero completos en BD
        Usuario clienteBD = usuarioRepository.findById(reservaParaGuardar.getCliente().getIdUsuario()).orElse(null);
        Usuario barberoBD = usuarioRepository.findById(reservaParaGuardar.getBarbero().getIdUsuario()).orElse(null);

        // Asignar objetos completos a la reserva
        reservaParaGuardar.setCliente(clienteBD);
        reservaParaGuardar.setBarbero(barberoBD);

        // Estado por defecto: PENDIENTE
        if(reservaParaGuardar.getEstado() == null) {
            reservaParaGuardar.setEstado("PENDIENTE");
        }

        // Guardar la reserva
        Reserva nuevaReserva = reservaRepository.save(reservaParaGuardar);

        // Guardar los servicios asociados a la reserva
        for (Integer idServicio : request.getIdServicios()) {
            ReservaServicio detalle = new ReservaServicio();
            detalle.setReserva(nuevaReserva);
            
            Servicio servicioBD = servicioRepository.findById(idServicio).orElse(null);
            detalle.setServicio(servicioBD);

            reservaServicioRepository.save(detalle);
        }

        return nuevaReserva;
    }

    // Obtener todas las reservas de un cliente específico (para "Mis Reservas")
    @GetMapping("/cliente/{clienteId}")
    public List<Reserva> obtenerReservasPorCliente(@PathVariable Integer clienteId) {
        return reservaRepository.findByCliente_IdUsuario(clienteId);
    }

    // Obtener todas las reservas asignadas a un barbero específico
    @GetMapping("/barbero/{barberoId}")
    public List<Reserva> obtenerReservasPorBarbero(@PathVariable Integer barberoId) {
        return reservaRepository.findByBarbero_IdUsuario(barberoId);
    }

    // Actualizar el estado de una reserva (ej. PENDIENTE → COMPLETADA)
    @PutMapping("/estado/{id}")
    public ResponseEntity<?> actualizarEstadoReserva(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Reserva> reservaFisica = reservaRepository.findById(id);

        if (reservaFisica.isPresent()) {
            Reserva reserva = reservaFisica.get();
            String nuevoEstado = body.get("estado");
            
            reserva.setEstado(nuevoEstado);
            Reserva reservaActualizada = reservaRepository.save(reserva);
            
            return ResponseEntity.ok(reservaActualizada);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La reserva no fue encontrada");
        }
    }
}