package com.madhouse.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.madhouse.app.models.Reserva;
import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardBarberoActivity extends AppCompatActivity {

    private TextView tvSaludo, tvSinCitas, tvServicio, tvFechaHora, tvCliente;
    private ImageView imgFotoPerfil;
    private CardView panelDetalleCita;
    private MaterialCardView cardMiPerfil, cardMisServicios, cardCrearServicio, cardCerrarSesion;

    private int idUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_barbero);

        inicializarVistas();

        // 1. Identidad: Recuperar datos guardados en Login
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", 0);
        String nombreLocal = prefs.getString("nombre", "Barbero");

        tvSaludo.setText("Hola, " + nombreLocal);

        configurarBotones(prefs);

        // 2. Traer datos frescos (Foto y Agenda)
        cargarDatosBarbero();
        cargarProximaCita();

        // Buscamos el botón por su ID
        TextView btnVolverInicio = findViewById(R.id.btnVolverAlInicio);

// Le asignamos la función de "terminar" la pantalla
        if (btnVolverInicio != null) {
            btnVolverInicio.setOnClickListener(v -> {
                // Esta instrucción cierra el Dashboard y te devuelve a la pantalla anterior
                finish();

                // Animación suave opcional
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void inicializarVistas() {
        tvSaludo = findViewById(R.id.tvSaludoBarbero);
        imgFotoPerfil = findViewById(R.id.imgFotoDashBarb);

        cardMiPerfil = findViewById(R.id.btnCardMiPerfilBarb);
        cardMisServicios = findViewById(R.id.btnCardMisServiciosBarb);
        cardCrearServicio = findViewById(R.id.btnCardCrearServicioBarb);
        cardCerrarSesion = findViewById(R.id.btnCardCerrarSesionBarb);

        tvSinCitas = findViewById(R.id.tvSinCitasBarb);
        panelDetalleCita = findViewById(R.id.panelDetalleCitaBarb);

        tvFechaHora = findViewById(R.id.tvCitaFechaHoraBarb);
        tvCliente = findViewById(R.id.tvCitaClienteBarb);
        tvServicio = findViewById(R.id.tvCitaServicioBarb);
    }

    private void configurarBotones(SharedPreferences prefs) {
        cardMiPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        cardMisServicios.setOnClickListener(v -> startActivity(new Intent(this, MisServiciosBarberoActivity.class)));
        cardCrearServicio.setOnClickListener(v -> startActivity(new Intent(this, CrearServicioActivity.class)));
        cardCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }

    // Método para la foto, idéntico al del cliente pero para el barbero
    private void cargarDatosBarbero() {
        RetrofitClient.getApiService().obtenerUsuarioPorId(idUsuarioActual).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body();
                    tvSaludo.setText("Hola, " + u.getNombre());

                    if (u.getFoto() != null && !u.getFoto().isEmpty()) {
                        try {
                            String base64Limpio = u.getFoto().replace("data:image/png;base64,", "")
                                    .replace("data:image/jpeg;base64,", "");
                            byte[] decodedString = Base64.decode(base64Limpio, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imgFotoPerfil.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            Log.e("DASH_BARB", "Error imagen: " + e.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {}
        });
    }

    // Traer citas específicas del BARBERO
    private void cargarProximaCita() {
        // ¡OJO AQUÍ! Usamos obtenerReservasBarbero
        RetrofitClient.getApiService().obtenerReservasBarbero(idUsuarioActual).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> citas = response.body();
                    Reserva proximaCitaActual = null;

                    for (Reserva r : citas) {
                        if ("PENDIENTE".equals(r.getEstado())) {
                            proximaCitaActual = r;
                            break;
                        }
                    }

                    if (proximaCitaActual != null) {
                        tvSinCitas.setVisibility(View.GONE);
                        panelDetalleCita.setVisibility(View.VISIBLE);

                        // Blindaje Anti-Crash
                        String fecha = proximaCitaActual.getFecha() != null ? proximaCitaActual.getFecha() : "--/--/----";
                        String hora = proximaCitaActual.getHora() != null ? formatearHora(proximaCitaActual.getHora()) : "--:--";
                        tvFechaHora.setText(fecha + " a las " + hora);

                        if (proximaCitaActual.getCliente() != null && proximaCitaActual.getCliente().getNombre() != null) {
                            tvCliente.setText(proximaCitaActual.getCliente().getNombre() + " " + proximaCitaActual.getCliente().getApellido());
                        } else {
                            tvCliente.setText("Cliente Anónimo");
                        }

                        if (proximaCitaActual.getServicio() != null && proximaCitaActual.getServicio().getNombre() != null) {
                            tvServicio.setText(proximaCitaActual.getServicio().getNombre());
                        } else {
                            tvServicio.setText("Servicio General");
                        }

                    } else {
                        mostrarSinCitas();
                    }
                } else {
                    mostrarSinCitas();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                mostrarSinCitas();
            }
        });
    }

    private void mostrarSinCitas() {
        panelDetalleCita.setVisibility(View.GONE);
        tvSinCitas.setVisibility(View.VISIBLE);
    }

    private String formatearHora(String hora24) {
        if (hora24 == null || hora24.isEmpty()) return "";
        try {
            String[] partes = hora24.split(":");
            int horas = Integer.parseInt(partes[0]);
            String minutos = partes[1];
            String ampm = horas >= 12 ? "PM" : "AM";
            horas = horas % 12;
            horas = horas == 0 ? 12 : horas;
            return horas + ":" + minutos + " " + ampm;
        } catch (Exception e) { return hora24; }
    }
}