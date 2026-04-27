package com.madhouse.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MisServiciosBarberoActivity extends AppCompatActivity {

    private Button btnProximas, btnHistorial;
    private LinearLayout contenedorCitas;
    private int idUsuarioActual;

    // Listas separadas para organizar la información
    private List<Reserva> listaProximas = new ArrayList<>();
    private List<Reserva> listaHistorial = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_servicios_barbero);

        inicializarVistas();

        // Recuperar Identidad del Barbero
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", 0);

        // Botón Volver
        findViewById(R.id.btnVolverDeServiciosBarb).setOnClickListener(v -> finish());

        // Eventos de las pestañas
        btnProximas.setOnClickListener(v -> activarPestana(true));
        btnHistorial.setOnClickListener(v -> activarPestana(false));

        // Cargar datos frescos
        cargarTodasLasCitas();
    }

    private void inicializarVistas() {
        btnProximas = findViewById(R.id.btnTabProximasBarb);
        btnHistorial = findViewById(R.id.btnTabHistorialBarb);
        contenedorCitas = findViewById(R.id.contenedorListaCitasBarb);
    }

    private void cargarTodasLasCitas() {
        // !!! LLAMADA CRÍTICA: Cambiada a obtenerReservasBarbero !!!
        RetrofitClient.getApiService().obtenerReservasBarbero(idUsuarioActual).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProximas.clear();
                    listaHistorial.clear();

                    // Clasificamos las reservas según la lógica de la web (imagen_11 vs imagen_12)
                    for (Reserva r : response.body()) {
                        if ("PENDIENTE".equals(r.getEstado())) {
                            listaProximas.add(r);
                        } else {
                            listaHistorial.add(r); // COMPLETADA o CANCELADA
                        }
                    }

                    // Mostramos las próximas por defecto
                    activarPestana(true);
                } else {
                    Log.e("API_ERROR_BARB", "Fallo al obtener citas: " + response.code());
                    mostrarMensajeVacio();
                }
            }
            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Log.e("API_ERROR_BARB", "Error de conexión: " + t.getMessage());
                mostrarMensajeVacio();
                Toast.makeText(MisServiciosBarberoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activarPestana(boolean esProximas) {
        // Estilos visuales de los botones (Dorado = Activo, Gris = Inactivo)
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

    // La "Fábrica" que infla el XML item_cita_barbero por cada reserva
    private void dibujarLista(List<Reserva> lista, boolean mostrarPeticionCompletar) {
        contenedorCitas.removeAllViews(); // Limpiar contenedor

        if (lista.isEmpty()) {
            mostrarMensajeVacio();
            return;
        }

        for (Reserva reserva : lista) {
            // 1. Inflar (clonar) el molde XML
            View vistaMolde = LayoutInflater.from(this).inflate(R.layout.item_cita_barbero, contenedorCitas, false);

            // 2. Buscar las vistas DENTRO del molde clonado
            TextView tvCliente = vistaMolde.findViewById(R.id.tvItemCliente);
            TextView tvFechaHora = vistaMolde.findViewById(R.id.tvItemFechaHoraBarb);
            TextView tvServicio = vistaMolde.findViewById(R.id.tvItemServicioBarb);
            TextView tvEstado = vistaMolde.findViewById(R.id.tvItemEstadoBarb);
            LinearLayout panelCompletar = vistaMolde.findViewById(R.id.panelCompletarCita);
            CheckBox checkCompletar = vistaMolde.findViewById(R.id.checkCompletarCita);

            // 3. Llenar los datos (Programación Defensiva Anti-Crash)

            // Validar Cliente
            if (reserva.getCliente() != null && reserva.getCliente().getNombre() != null) {
                tvCliente.setText(reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido());
            } else {
                tvCliente.setText("Cliente Anónimo");
            }

            // Validar Fecha y Hora
            String fecha = reserva.getFecha() != null ? reserva.getFecha() : "--";
            String hora = reserva.getHora() != null ? reserva.getHora() : "--";
            tvFechaHora.setText(fecha + " - " + hora);

            // Validar Servicio
            if (reserva.getServicio() != null) {
                tvServicio.setText("Servicio: " + reserva.getServicio().getNombre());
            } else {
                tvServicio.setText("Servicio General");
            }

            // 4. Configurar el Estado (Adaptando Colores de imagen_12.png)
            String estadoStr = reserva.getEstado() != null ? reserva.getEstado() : "PENDIENTE";
            tvEstado.setText(estadoStr);

            if ("COMPLETADA".equals(estadoStr)) {
                tvEstado.setBackgroundColor(Color.parseColor("#2E7D32")); // Verde oscuro
                tvEstado.getBackground().setColorFilter(Color.parseColor("#2E7D32"), PorterDuff.Mode.SRC_IN);
            } else if ("CANCELADA".equals(estadoStr)) {
                tvEstado.setBackgroundColor(Color.parseColor("#C62828")); // Rojo oscuro
                tvEstado.getBackground().setColorFilter(Color.parseColor("#C62828"), PorterDuff.Mode.SRC_IN);
            } else {
                tvEstado.setBackgroundColor(Color.parseColor("#555555")); // Gris para PENDIENTE
                tvEstado.getBackground().setColorFilter(Color.parseColor("#555555"), PorterDuff.Mode.SRC_IN);
            }

            // 5. Configurar Lógica de Marcar como Completado (Solo en Próximas)
            if (mostrarPeticionCompletar) {
                panelCompletar.setVisibility(View.VISIBLE);
                panelCompletar.setOnClickListener(v -> mostrarDialogoCompletar(reserva, checkCompletar));
            } else {
                panelCompletar.setVisibility(View.GONE);
            }

            // 6. Pegar el molde lleno en el contenedor de la pantalla
            contenedorCitas.addView(vistaMolde);
        }
    }

    private void mostrarMensajeVacio() {
        TextView tvVacio = new TextView(this);
        tvVacio.setText("No hay citas registradas en esta sección.");
        tvVacio.setTextColor(Color.WHITE);
        tvVacio.setTextSize(16);
        tvVacio.setGravity(Gravity.CENTER);
        tvVacio.setPadding(0, 50, 0, 0);
        contenedorCitas.addView(tvVacio);
    }

    // --- LÓGICA DE COMPLETAR CITA ---

    private void mostrarDialogoCompletar(Reserva reserva, CheckBox checkBox) {
        new AlertDialog.Builder(this)
                .setTitle("Completar Servicio")
                .setMessage("¿Deseas marcar esta cita con " + reserva.getCliente().getNombre() + " como COMPLETADA?")
                .setPositiveButton("Sí, Completar", (dialog, which) -> ejecutarPeticionCompletar(reserva, checkBox))
                .setNegativeButton("No", null)
                .show();
    }

    private void ejecutarPeticionCompletar(Reserva reserva, CheckBox checkBox) {
        // Marcamos el checkbox visualmente antes de la petición para dar feedback rápido
        checkBox.setChecked(true);
        checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C69C3B"))); // Dorado

        // Objeto con el nuevo estado
        Reserva actualizada = new Reserva();
        actualizada.setEstado("COMPLETADA");

        // !!! LLAMADA CRÍTICA: Asumiendo que existe un endpoint de completar !!!
        // Si no existe, puedes reusar el PUT de cancelar pero cambiando el estado en Java antes de enviar.
        RetrofitClient.getApiService().completarReserva(reserva.getIdReserva(), actualizada).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(MisServiciosBarberoActivity.this, "Servicio registrado como completado", Toast.LENGTH_SHORT).show();
                    cargarTodasLasCitas(); // Recargar para moverla al historial (imagen_12)
                } else {
                    Log.e("API_ERROR_BARB", "Fallo al completar cita: " + response.code());
                    checkBox.setChecked(false); // Revertir check visual si falla
                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC"))); // Gris
                    Toast.makeText(MisServiciosBarberoActivity.this, "Error en el servidor al completar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                checkBox.setChecked(false);
                checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                Toast.makeText(MisServiciosBarberoActivity.this, "Error de red al completar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}