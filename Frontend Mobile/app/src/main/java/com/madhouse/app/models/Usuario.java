package com.madhouse.app.models;

public class Usuario {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena; // Necesario para el Registro/Login
    private String telefono;
    private String direccion;
    private String fechaNacimiento; // Manejado como String para compatibilidad con JSON
    private String rol;
    private String especialidad;
    private String foto; // LONGTEXT en Base64
    private String biografia;

    private Integer puntos;

    // Constructor vacío (Obligatorio para que Gson pueda instanciar la clase)
    public Usuario() {
    }

    // --- GETTERS ---
    // Los usamos para leer la información de la API y mostrarla en las Cards

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getRol() { return rol; }
    public String getEspecialidad() { return especialidad; }
    public String getFoto() { return foto; }
    public String getBiografia() { return biografia; }

    // --- SETTERS ---
    // Serán vitales cuando construyamos la pantalla de Registro

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setRol(String rol) { this.rol = rol; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }
}