package com.madhouse.madhouse_app.repo;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.madhouse.madhouse_app.model.Reserva;

// Un Repository accede a la tabla "reserva" en MySQL
// Spring Data JPA genera automáticamente las consultas SQL basadas en el nombre del método

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    
    // Busca todas las reservas de un cliente específico
    // Caso de uso: Mostrar todas las citas del cliente en "Mis Reservas"
    // SQL generado: SELECT * FROM reserva WHERE id_cliente = ?
    List<Reserva> findByCliente_IdUsuario(Integer idCliente);

    // Busca todas las reservas asignadas a un barbero específico
    // Caso de uso: Mostrar todas las citas del barbero en su dashboard
    // SQL generado: SELECT * FROM reserva WHERE id_barbero = ?
    List<Reserva> findByBarbero_IdUsuario(Integer idBarbero);
}
