package com.madhouse.madhouse_app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madhouse.madhouse_app.model.Servicio;
import com.madhouse.madhouse_app.repo.ServicioRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


// Controlador para gestionar servicios (CRUD: crear, leer, actualizar, eliminar)

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    // Obtener todos los servicios (para mostrar en Home y Servicios)
    @GetMapping
    public List<Servicio> listarTodosLosServicios() {
        return servicioRepository.findAll();
    }
    
    // Alternativa: también lista todos los servicios
    @GetMapping("/servicio")
    public List<Servicio> getFindAll() {
        return servicioRepository.findAll();
    }
    
    // Crear un nuevo servicio
    @PostMapping("/crear")
    public Servicio createServicio(@RequestBody Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    // Actualizar un servicio existente por su ID
    @PutMapping("/actualizar/{id}")
    public Servicio actualizarServicio(@PathVariable Integer id, @RequestBody Servicio servicioActualizado) {
        return servicioRepository.findById(id)
                .map(servicio -> {
                    servicio.setNombre(servicioActualizado.getNombre());
                    servicio.setDescripcion(servicioActualizado.getDescripcion());
                    servicio.setPrecio(servicioActualizado.getPrecio());
                    servicio.setDuracion(servicioActualizado.getDuracion());
                    return servicioRepository.save(servicio);
                })
                .orElse(null);
    }

    // Eliminar un servicio por su ID
    @DeleteMapping("/eliminar/{id}")
    public void eliminarServicio(@PathVariable Integer id) {
        servicioRepository.deleteById(id);
    }
}
