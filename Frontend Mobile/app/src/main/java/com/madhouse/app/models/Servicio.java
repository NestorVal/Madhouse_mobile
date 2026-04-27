package com.madhouse.app.models;

public class Servicio {
    private Integer idServicio;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracion;
    private String foto; // Base64

    // Getters
    public Integer getIdServicio() { return idServicio; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecio() { return precio; }
    public Integer getDuracion() { return duracion; }
    public String getFoto() { return foto; }

    // SETTERS

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}