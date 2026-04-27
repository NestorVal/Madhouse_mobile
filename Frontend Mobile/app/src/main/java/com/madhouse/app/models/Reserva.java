package com.madhouse.app.models;

public class Reserva {
    private Integer idReserva;
    private String fecha;
    private String hora;
    private String estado;

    // Spring Boot suele devolver los objetos anidados, así que los mapeamos aquí
    private Usuario cliente;
    private Usuario barbero;
    private Servicio servicio;

    // Getters
    public Integer getIdReserva() { return idReserva; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getEstado() { return estado; }
    public Usuario getCliente() { return cliente; }
    public Usuario getBarbero() { return barbero; }
    public Servicio getServicio() { return servicio; }

    // SETTERS
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // SETTERS

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public void setBarbero(Usuario barbero) {
        this.barbero = barbero;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}