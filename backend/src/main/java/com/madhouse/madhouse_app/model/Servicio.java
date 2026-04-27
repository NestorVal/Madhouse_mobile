package com.madhouse.madhouse_app.model;

import jakarta.persistence.*;

@Entity // Indica que esta clase es una entidad de JPA
@Table(name = "servicio") // Especifica el nombre de la tabla en la base de datos
public class Servicio{ // Define la clase Servicio como una entidad de JPA

    @Id // Indica que el campo id_servicio es la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica que el valor de id_servicio se generará automáticamente por la base de datos
    @Column(name = "id_servicio") // Especifica el nombre de la columna en la base de datos
    private Integer idServicio; // Define el campo id_servicio como un Integer

    @Column(name = "nombre") // Especifica el nombre de la columna en la base de datos
    private String nombre; // Define el campo nombre como un String

    @Column(name = "descripcion") // Especifica el nombre de la columna en la base de datos
    private String descripcion; // Define el campo descripcion como un String

    @Column(name = "precio")
    private Double precio; // Define el campo precio como un Double

    @Column(name = "duracion")
    private Integer duracion;

    @Lob
    @Column(name = "foto", columnDefinition = "LONGTEXT")
    private String foto;

    // Getters y Setters
    public Integer getIdServicio() { // Define el método getIdServicio para obtener el valor de id_servicio
        return idServicio; // Devuelve el valor de id_servicio
    }

    public void setIdServicio(Integer idServicio) { // Define el método setIdServicio para establecer el valor de idServicio
        this.idServicio = idServicio; // Asigna el valor de idServicio al campo idServicio de la clase
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public String getFoto() { 
        return foto; 
    }

    public void setFoto(String foto) { 
        this.foto = foto; 
    }
}
