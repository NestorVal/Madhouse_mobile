package com.madhouse.madhouse_app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.madhouse.madhouse_app.model.Servicio;

// Un Repository accede a la tabla "servicio" en MySQL
// Los servicios son: Corte, Arreglo de barba, Tratamiento, etc.

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    
    // Usamos solo los métodos heredados de JpaRepository:
    // findAll() → Devuelve todos los servicios (para mostrar en Home y Servicios)
    // findById(Integer) → Busca un servicio por ID
    // save(Servicio) → Guarda un nuevo servicio
    // deleteById(Integer) → Elimina un servicio
}
