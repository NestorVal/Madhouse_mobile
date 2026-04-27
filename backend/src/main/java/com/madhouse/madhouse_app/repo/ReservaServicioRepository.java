package com.madhouse.madhouse_app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.madhouse.madhouse_app.model.ReservaServicio;

// Un Repository accede a la tabla "reservaservicio" en MySQL
// Esta tabla guarda la relación entre cada reserva y sus servicios
// Una reserva puede tener múltiples servicios (corte, barba, arreglo, etc)

@Repository
public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, Integer> {
    
    // Por ahora, solo usamos los métodos heredados de JpaRepository:
    // save(ReservaServicio) → Guarda un detalle de servicio en una reserva
    // findById(Integer) → Busca por ID
    // findAll() → Trae todos
    // deleteById(Integer) → Elimina por ID
}