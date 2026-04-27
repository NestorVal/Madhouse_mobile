package com.madhouse.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.madhouse.app.models.Reserva;
import com.madhouse.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisReservasActivity extends AppCompatActivity {

    private Button btnProximas, btnHistorial;
    private LinearLayout contenedorReservas;
    private int idUsuarioActual;

    // Listas separadas para organizar la información
    private List<Reserva> listaProximas = new ArrayList<>();
    private List<Reserva> listaHistorial = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        btnProximas = findViewById(R.id.btnTabProximas);
        btnHistorial = findViewById(R.id.btnTabHistorial);
        contenedorReservas = findViewById(R.id.contenedorListaReservas);

        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", 0);

        findViewById(R.id.btnVolverDeReservas).setOnClickListener(v -> finish());

        // Eventos de las pestañas
        btnProximas.setOnClickListener(v -> activarPestana(true));
        btnHistorial.setOnClickListener(v -> activarPestana(false));

        cargarTodasLasReservas();
    }

    private void cargarTodasLasReservas() {
        RetrofitClient.getApiService().obtenerReservasCliente(idUsuarioActual).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProximas.clear();
                    listaHistorial.clear();

                    // Clasificamos las reservas
                    for (Reserva r : response.body()) {
                        if ("PENDIENTE".equals(r.getEstado())) {
                            listaProximas.add(r);
                        } else {
                            listaHistorial.add(r); // COMPLETADA o CANCELADA
                        }
                    }

                    // Mostramos las próximas por defecto
                    activarPestana(true);
                }
            }
            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(MisReservasActivity.this, "Error cargando historial", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activarPestana(boolean esProximas) {
        // Estilos visuales de los botones
        if (esProximas) {
            btnProximas.setBackgroundColor(Color.parseColor("#333333"));
            btnProximas.setTextColor(Color.parseColor("#C69C3B"));
            btnHistorial.setBackgroundColor(Color.parseColor("#1A1A1A"));
            btnHistorial.setTextColor(Color.parseColor("#888888"));
            dibujarLista(listaProximas, true);
        } else {
            btnHistorial.setBackgroundColor(Color.parseColor("#333333"));
            btnHistorial.setTextColor(Color.parseColor("#C69C3B"));
            btnProximas.setBackgroundColor(Color.parseColor("#1A1A1A"));
            btnProximas.setTextColor(Color.parseColor("#888888"));
            dibujarLista(listaHistorial, false);
        }
    }

    // La "Fábrica" que clona el XML por cada reserva
    private void dibujarLista(List<Reserva> lista, boolean mostrarBotonCancelar) {
        contenedorReservas.removeAllViews(); // Limpiar contenedor

        if (lista.isEmpty()) {
            TextView tvVacio = new TextView(this);
            tvVacio.setText("No hay reservas en esta sección.");
            tvVacio.setTextColor(Color.WHITE);
            tvVacio.setPadding(0, 50, 0, 0);
            contenedorReservas.addView(tvVacio);
            return;
        }

        for (Reserva reserva : lista) {
            // 1. Inflar (clonar) el molde XML
            View vistaMolde = LayoutInflater.from(this).inflate(R.layout.item_reserva, contenedorReservas, false);

            // 2. Buscar las vistas DENTRO de ese molde clonado
            TextView tvFechaHora = vistaMolde.findViewById(R.id.tvItemFechaHora);
            TextView tvServicio = vistaMolde.findViewById(R.id.tvItemServicio);
            TextView tvBarbero = vistaMolde.findViewById(R.id.tvItemBarbero);
            TextView tvEstado = vistaMolde.findViewById(R.id.tvItemEstado);
            Button btnCancelar = vistaMolde.findViewById(R.id.btnItemCancelar);

            // 3. Llenar los datos (Programación Defensiva)
            String fecha = reserva.getFecha() != null ? reserva.getFecha() : "--";
            String hora = reserva.getHora() != null ? reserva.getHora() : "--";
            tvFechaHora.setText(fecha + " - " + hora);

            if (reserva.getServicio() != null) tvServicio.setText(reserva.getServicio().getNombre());
            if (reserva.getBarbero() != null) tvBarbero.setText("Barbero: " + reserva.getBarbero().getNombre());

            // 4. Configurar el Estado (Color)
            String estadoStr = reserva.getEstado() != null ? reserva.getEstado() : "DESCONOCIDO";
            tvEstado.setText(estadoStr);

            if ("COMPLETADA".equals(estadoStr)) {
                tvEstado.setBackgroundColor(Color.parseColor("#2E7D32")); // Verde oscuro
            } else if ("CANCELADA".equals(estadoStr)) {
                tvEstado.setBackgroundColor(Color.parseColor("#C62828")); // Rojo oscuro
            } else {
                tvEstado.setBackgroundColor(Color.parseColor("#555555")); // Gris
            }

            // 5. Configurar Botón Cancelar
            if (mostrarBotonCancelar) {
                btnCancelar.setVisibility(View.VISIBLE);
                btnCancelar.setOnClickListener(v -> mostrarDialogoCancelar(reserva));
            } else {
                btnCancelar.setVisibility(View.GONE);
            }

            // 6. Pegar el molde lleno en la pantalla
            contenedorReservas.addView(vistaMolde);
        }
    }

    private void mostrarDialogoCancelar(Reserva reserva) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Cita")
                .setMessage("¿Estás seguro de cancelar esta reserva?")
                .setPositiveButton("Sí", (dialog, which) -> ejecutarCancelacion(reserva))
                .setNegativeButton("No", null)
                .show();
    }

    private void ejecutarCancelacion(Reserva reserva) {
        Reserva actualizada = new Reserva();
        actualizada.setEstado("CANCELADA");

        RetrofitClient.getApiService().cancelarReserva(reserva.getIdReserva(), actualizada).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(MisReservasActivity.this, "Cita Cancelada", Toast.LENGTH_SHORT).show();
                    cargarTodasLasReservas(); // Recargar para moverla al historial
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}