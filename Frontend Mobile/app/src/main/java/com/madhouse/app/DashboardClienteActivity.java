package com.madhouse.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.madhouse.app.models.Reserva;
import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardClienteActivity extends AppCompatActivity {

    private TextView tvSaludo, tvSinCitas, tvServicio, tvFechaHora, tvBarbero, tvPuntos;
    private ImageView imgFotoPerfil;
    private LinearLayout panelDetalleCita;
    private Button btnCancelar, btnReservaAhora; // Agregamos el botón del banner
    private CardView cardMiPerfil, cardMisReservas, cardEditarPerfil, cardCerrarSesion, cardFidelizacion;

    private int idUsuarioActual;
    private Reserva proximaCitaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_cliente);

        // 1. Inicializar componentes
        inicializarVistas();

        // 2. Cargar identidad local
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", 0);
        String nombreLocal = prefs.getString("nombre", "Cliente");
        tvSaludo.setText("Hola, " + nombreLocal);

        // 3. Configurar eventos de botones
        configurarBotones(prefs);

        // 4. Cargar datos desde el servidor
        cargarDatosUsuario();
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
        tvSaludo = findViewById(R.id.tvSaludoCliente);
        imgFotoPerfil = findViewById(R.id.imgFotoDashCliente);
        tvPuntos = findViewById(R.id.tvPuntosCli);

        // Botones del Menú (Cards)
        cardMiPerfil = findViewById(R.id.btnCardMiPerfil);
        cardMisReservas = findViewById(R.id.btnCardMisReservas);
        cardEditarPerfil = findViewById(R.id.btnCardEditarPerfil);
        cardCerrarSesion = findViewById(R.id.btnCardCerrarSesion);
        cardFidelizacion = findViewById(R.id.btnCardFidelizacion);

        // Botón del Banner (Asegúrate de que este ID sea el del botón "Reserva tu cita ahora" en tu XML)
        btnReservaAhora = findViewById(R.id.btnReservarCita);

        // Sección de Próxima Cita
        tvSinCitas = findViewById(R.id.tvSinCitasCli);
        panelDetalleCita = findViewById(R.id.panelDetalleCitaCli);
        tvServicio = findViewById(R.id.tvCitaServicioCli);
        tvFechaHora = findViewById(R.id.tvCitaFechaHoraCli);
        tvBarbero = findViewById(R.id.tvCitaBarberoCli);
        btnCancelar = findViewById(R.id.btnCancelarCitaCli);
    }

    private void configurarBotones(SharedPreferences prefs) {
        // Navegación a Reservas (Banner)
        if (btnReservaAhora != null) {
            btnReservaAhora.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReservasActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Navegación a Perfil y Reservas
        cardMiPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        cardMisReservas.setOnClickListener(v -> startActivity(new Intent(this, MisReservasActivity.class)));
        cardEditarPerfil.setOnClickListener(v -> startActivity(new Intent(this, EditarPerfilActivity.class)));

        cardFidelizacion.setOnClickListener(v ->
                Toast.makeText(this, "Puntos acumulados: " + tvPuntos.getText(), Toast.LENGTH_SHORT).show()
        );

        // Cerrar Sesión
        cardCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(DashboardClienteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Cancelar Cita
        btnCancelar.setOnClickListener(v -> confirmarCancelacion());
    }

    // --- LÓGICA DE DATOS (RETROFIT) ---

    private void cargarDatosUsuario() {
        RetrofitClient.getApiService().obtenerUsuarioPorId(idUsuarioActual).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body();
                    tvSaludo.setText("Hola, " + u.getNombre());
                    if (u.getPuntos() != null) tvPuntos.setText(String.valueOf(u.getPuntos()));

                    if (u.getFoto() != null && !u.getFoto().isEmpty()) {
                        try {
                            String base64Limpio = u.getFoto().replace("data:image/png;base64,", "")
                                    .replace("data:image/jpeg;base64,", "");
                            byte[] decodedString = Base64.decode(base64Limpio, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imgFotoPerfil.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            Log.e("DASHBOARD_CLI", "Error imagen: " + e.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("DASHBOARD_CLI", "Fallo servidor: " + t.getMessage());
            }
        });
    }

    private void cargarProximaCita() {
        RetrofitClient.getApiService().obtenerReservasCliente(idUsuarioActual).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> citas = response.body();
                    proximaCitaActual = null;

                    for (Reserva r : citas) {
                        if ("PENDIENTE".equals(r.getEstado())) {
                            proximaCitaActual = r;
                            break;
                        }
                    }

                    if (proximaCitaActual != null) {
                        tvSinCitas.setVisibility(View.GONE);
                        panelDetalleCita.setVisibility(View.VISIBLE);

                        if (proximaCitaActual.getServicio() != null)
                            tvServicio.setText(proximaCitaActual.getServicio().getNombre());

                        if (proximaCitaActual.getBarbero() != null)
                            tvBarbero.setText(proximaCitaActual.getBarbero().getNombre());

                        String fecha = proximaCitaActual.getFecha() != null ? proximaCitaActual.getFecha() : "--/--/----";
                        String hora = proximaCitaActual.getHora() != null ? formatearHora(proximaCitaActual.getHora()) : "--:--";
                        tvFechaHora.setText(fecha + " a las " + hora);
                    } else {
                        mostrarSinCitas();
                    }
                } else { mostrarSinCitas(); }
            }
            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) { mostrarSinCitas(); }
        });
    }

    private void confirmarCancelacion() {
        if (proximaCitaActual == null) return;
        new AlertDialog.Builder(this)
                .setTitle("¿Cancelar Cita?")
                .setMessage("¿Estás seguro de que deseas cancelar esta cita?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> ejecutarCancelacion())
                .setNegativeButton("No", null)
                .show();
    }

    private void ejecutarCancelacion() {
        Reserva estadoCancelado = new Reserva();
        estadoCancelado.setEstado("CANCELADA");
        RetrofitClient.getApiService().cancelarReserva(proximaCitaActual.getIdReserva(), estadoCancelado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DashboardClienteActivity.this, "Cita cancelada", Toast.LENGTH_SHORT).show();
                    mostrarSinCitas();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardClienteActivity.this, "Error al cancelar", Toast.LENGTH_SHORT).show();
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