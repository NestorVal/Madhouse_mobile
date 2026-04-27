package com.madhouse.madhouse_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resena")
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Integer idResena;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "calificacion")
    private Integer calificacion;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "id_barbero")
    private Usuario barbero;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    // Getters y Setters 
    
    public Integer getIdResena() {
        return idResena;
    }
    public void setIdResena(Integer idResena){
        this.idResena = idResena;
    }

    public String getComentario () {
        return comentario;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }

    public Integer getCalificacion(){
        return calificacion;
    }
    public void setCalificacion(Integer calificacion){
        this.calificacion = calificacion;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente){
        this.cliente = cliente;
    }

    public Usuario getBarbero(){
        return barbero;
    }
    public void setBarbero(Usuario barbero){
        this.barbero = barbero;
    }

    public Servicio getServicio(){
        return servicio;
    }
    public void setServicio(Servicio servicio){
        this.servicio = servicio;
    }
}


