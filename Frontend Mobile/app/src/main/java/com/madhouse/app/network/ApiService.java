package com.madhouse.app.network;

import com.madhouse.app.models.PaqueteReserva;
import com.madhouse.app.models.Reserva;
import com.madhouse.app.models.Servicio;
import com.madhouse.app.models.Usuario; // Agrega esta importación
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    //Endpoint para traer los servicios
    @GET("api/servicios")
    Call<List<Servicio>> obtenerServicios();

    // Endpoint para traer a los barberos
    @GET("api/usuarios/barberos")
    Call<List<Usuario>> obtenerBarberos();

    // NUEVO: Enviar credenciales para Iniciar Sesión
    @POST("api/auth/login")
    Call<Usuario> login(@Body Usuario credenciales);

    // NUEVO: Enviar un nuevo usuario para Registrarse
    @POST("api/auth/registrar")
    Call<Usuario> registrarUsuario(@Body Usuario nuevoUsuario);

    @GET("api/usuarios/{id}")
    Call<Usuario> obtenerUsuarioPorId(@Path("id") int id);

    @PUT("api/usuarios/actualizar/{id}")
    Call<Usuario> actualizarUsuario(@Path("id") int id, @Body Usuario usuario);

    // Endpoint para citas del barbero
    @GET("api/reservas/barbero/{id}")
    Call<List<Reserva>> obtenerReservasBarbero(@Path("id") int idBarbero);

    // Endpoint para citas del cliente
    @GET("api/reservas/cliente/{id}")
    Call<List<Reserva>> obtenerReservasCliente(@Path("id") int idCliente);

    // Endpoint para cancelar (Actualizar estado)
    @PUT("api/reservas/estado/{id}")
    Call<Void> cancelarReserva(@Path("id") int idReserva, @Body Reserva estadoActualizado);

    @POST("api/servicios/crear") // Reemplaza con la ruta correcta de tu Spring Boot
    Call<Servicio> crearServicio(@Body Servicio servicio);
    // Debajo de tus otros métodos de reservas
    @PUT("api/reservas/estado/{id}")
    Call<Void> completarReserva(@Path("id") int idReserva, @Body Reserva reserva);

    // Debajo de obtenerServicios()...

    @GET("api/usuarios")
    Call<List<Usuario>> obtenerUsuarios();

    @POST("api/reservas/crear")
    Call<Void> crearReservaCompleta(@Body PaqueteReserva paquete);
}