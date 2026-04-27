package com.madhouse.app.models;

import java.util.List;

public class PaqueteReserva {
    private Reserva reserva;
    private List<Integer> idServicios;

    public PaqueteReserva(Reserva reserva, List<Integer> idServicios) {
        this.reserva = reserva;
        this.idServicios = idServicios;
    }
    // Getters y Setters
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public List<Integer> getIdServicios() { return idServicios; }
    public void setIdServicios(List<Integer> idServicios) { this.idServicios = idServicios; }
}