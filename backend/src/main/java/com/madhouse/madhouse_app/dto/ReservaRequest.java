package com.madhouse.madhouse_app.dto;

import com.madhouse.madhouse_app.model.Reserva;
import java.util.List;

// ReservaRequest agrupa dos datos: la reserva principal y la lista de servicios
// Spring Boot convierte automáticamente el JSON de React en este objeto

public class ReservaRequest {
    
    // Datos principales de la cita (fecha, hora, cliente, barbero, estado)
    private Reserva reserva;
    
    // Lista de IDs de servicios seleccionados (ej: [1, 3, 5])
    private List<Integer> idServicios;

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public List<Integer> getIdServicios() {
        return idServicios;
    }

    public void setIdServicios(List<Integer> idServicios) {
        this.idServicios = idServicios;
    }
}