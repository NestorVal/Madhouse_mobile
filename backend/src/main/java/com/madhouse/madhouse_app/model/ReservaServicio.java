package com.madhouse.madhouse_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservaservicio")
public class ReservaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_servicio") // Ajusta este nombre si tu llave primaria en MySQL se llama distinto (ej: solo "id")
    private Integer idReservaServicio;

    @Column(name = "duracion")
    private Integer duracion;

    @Column(name = "valor")
    private Double valor;
    
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    // Getters y Setters
    public Integer getIdReservaServicio() {
        return idReservaServicio;
    }

    public void setIdReservaServicio(Integer idReservaServicio) {
        this.idReservaServicio = idReservaServicio;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}