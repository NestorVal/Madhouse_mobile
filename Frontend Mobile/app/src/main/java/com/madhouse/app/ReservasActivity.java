package com.madhouse.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.madhouse.app.models.PaqueteReserva;
import com.madhouse.app.models.Reserva;
import com.madhouse.app.models.Servicio;
import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasActivity extends AppCompatActivity {

    private int pasoActual = 1;
    private LinearLayout contenedor;
    private TextView tvPasoTitulo;
    private ProgressBar progressBar;
    private Button btnSiguiente, btnAtras;

    // Estado de la reserva (Como tu datosReserva de React)
    private Servicio servicioSeleccionado;
    private Usuario barberoSeleccionado;
    private String fechaSeleccionada = "2026-04-25"; // Simplificado para el ejemplo
    private String horaSeleccionada;
    private String metodoPago;

    private List<Servicio> listaServicios = new ArrayList<>();
    private List<Usuario> listaBarberos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        contenedor = findViewById(R.id.contenedorPasos);
        tvPasoTitulo = findViewById(R.id.tvPasoTitulo);
        progressBar = findViewById(R.id.pbReserva);
        btnSiguiente = findViewById(R.id.btnSiguienteReserva);
        btnAtras = findViewById(R.id.btnAtrasReserva);

        cargarDatosIniciales();

        btnSiguiente.setOnClickListener(v -> {
            if (pasoActual < 5) {
                pasoActual++;
                actualizarInterfaz();
            } else {
                enviarReservaFinal();
            }
        });

        btnAtras.setOnClickListener(v -> {
            if (pasoActual > 1) {
                pasoActual--;
                actualizarInterfaz();
            }
        });

        int idInicial = getIntent().getIntExtra("ID_SERVICIO_INICIAL", -1);
        if (idInicial != -1) {
            // Buscar el servicio en la lista y pre-seleccionarlo
            pasoActual = 2; // Iniciar directamente en barberos
        }
    }

    private void cargarDatosIniciales() {
        // Cargar Servicios
        RetrofitClient.getApiService().obtenerServicios().enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                if (response.isSuccessful()) {
                    listaServicios = response.body();
                    actualizarInterfaz(); // Dibujar el paso 1
                }
            }
            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {}
        });

        // Cargar Barberos
        RetrofitClient.getApiService().obtenerUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    for (Usuario u : response.body()) {
                        if ("ROLE_BARBERO".equals(u.getRol())) listaBarberos.add(u);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {}
        });
    }

    private void actualizarInterfaz() {
        contenedor.removeAllViews();
        progressBar.setProgress(pasoActual);
        btnAtras.setVisibility(pasoActual > 1 ? View.VISIBLE : View.GONE);

        switch (pasoActual) {
            case 1:
                tvPasoTitulo.setText("Paso 1: Selecciona el servicio");
                dibujarServicios();
                break;
            case 2:
                tvPasoTitulo.setText("Paso 2: Elige a tu barbero");
                dibujarBarberos();
                break;
            case 3:
                tvPasoTitulo.setText("Paso 3: ¿Cuándo nos visitas?");
                dibujarHorarios();
                break;
            case 4:
                tvPasoTitulo.setText("Paso 4: Forma de Pago");
                dibujarPagos();
                break;
            case 5:
                tvPasoTitulo.setText("Paso 5: Resumen");
                btnSiguiente.setText("¡CONFIRMAR CITA!");
                dibujarResumen();
                break;
        }
    }

    // --- MÉTODOS DE DIBUJO (Similares al .map de React) ---

    private void dibujarServicios() {
        for (Servicio s : listaServicios) {
            View card = LayoutInflater.from(this).inflate(R.layout.item_reserva_opcion, contenedor, false);
            TextView txt = card.findViewById(R.id.tvOpNombre);
            txt.setText(s.getNombre() + " - $" + s.getPrecio());

            card.setOnClickListener(v -> {
                servicioSeleccionado = s;
                Toast.makeText(this, "Seleccionado: " + s.getNombre(), Toast.LENGTH_SHORT).show();
            });
            contenedor.addView(card);
        }
    }

    private void dibujarBarberos() {
        for (Usuario b : listaBarberos) {
            View card = LayoutInflater.from(this).inflate(R.layout.item_reserva_opcion, contenedor, false);
            TextView txt = card.findViewById(R.id.tvOpNombre);
            txt.setText(b.getNombre() + " " + b.getApellido());

            card.setOnClickListener(v -> {
                barberoSeleccionado = b;
                Toast.makeText(this, "Barbero: " + b.getNombre(), Toast.LENGTH_SHORT).show();
            });
            contenedor.addView(card);
        }
    }

    private void dibujarHorarios() {
        // 1. Crear un botón para abrir el Calendario
        Button btnCalendario = new Button(this);
        btnCalendario.setText(fechaSeleccionada == null ? "Seleccionar Fecha" : "Fecha: " + fechaSeleccionada);
        btnCalendario.setBackgroundColor(Color.parseColor("#C69C3B"));
        btnCalendario.setTextColor(Color.WHITE);

        btnCalendario.setOnClickListener(v -> abrirCalendario());
        contenedor.addView(btnCalendario);

        // 2. Solo mostrar las horas si ya eligió una fecha
        if (fechaSeleccionada != null) {
            TextView tvSub = new TextView(this);
            tvSub.setText("\nSelecciona la hora:");
            tvSub.setTextColor(Color.BLACK);
            contenedor.addView(tvSub);

            String[] horas = {"14:00", "15:30", "17:00", "18:30"};
            for (String h : horas) {
                Button b = new Button(this);
                b.setText(h);
                // Si la hora está seleccionada, pintarla de dorado
                if (h.equals(horaSeleccionada)) b.setBackgroundColor(Color.LTGRAY);

                b.setOnClickListener(v -> {
                    horaSeleccionada = h;
                    actualizarInterfaz(); // Refrescar para marcar la selección
                });
                contenedor.addView(b);
            }
        }
    }

    // Método para abrir el calendario de Android
    private void abrirCalendario() {
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Guardamos la fecha en formato YYYY-MM-DD para el backend
            fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
            actualizarInterfaz(); // Redibujamos para que aparezcan las horas
        }, año, mes, dia);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis()); // No permitir fechas pasadas
        dpd.show();
    }

    private void dibujarPagos() {
        String[] metodos = {"Efectivo", "Tarjeta", "App (Nequi)"};
        for (String m : metodos) {
            Button b = new Button(this);
            b.setText(m);
            b.setOnClickListener(v -> metodoPago = m);
            contenedor.addView(b);
        }
    }

    private void dibujarResumen() {
        TextView tv = new TextView(this);
        tv.setText("Servicio: " + servicioSeleccionado.getNombre() +
                "\nBarbero: " + barberoSeleccionado.getNombre() +
                "\nFecha: " + fechaSeleccionada + " " + horaSeleccionada +
                "\nPago: " + metodoPago +
                "\nTotal: $" + servicioSeleccionado.getPrecio());
        tv.setTextSize(18);
        contenedor.addView(tv);
    }

    private void enviarReservaFinal() {
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", MODE_PRIVATE);
        int idCliente = prefs.getInt("idUsuario", 0);

        // 1. Construir la Reserva interna (Igual que en tu React)
        Reserva r = new Reserva();
        r.setFecha(fechaSeleccionada);
        r.setHora(horaSeleccionada); // Asegúrate que venga en formato HH:mm
        r.setEstado("PENDIENTE");

        // Asignar Cliente (Solo el ID)
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(idCliente);
        r.setCliente(cliente);

        // Asignar Barbero (Solo el ID)
        Usuario barbero = new Usuario();
        barbero.setIdUsuario(barberoSeleccionado.getIdUsuario());
        r.setBarbero(barbero);

        // 2. Crear la lista de IDs de servicios
        List<Integer> ids = new ArrayList<>();
        ids.add(servicioSeleccionado.getIdServicio());

        // 3. Empaquetar todo
        PaqueteReserva paquete = new PaqueteReserva(r, ids);

        // 4. EL ENVÍO REAL
        btnSiguiente.setEnabled(false);
        btnSiguiente.setText("PROCESANDO...");

        RetrofitClient.getApiService().crearReservaCompleta(paquete).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReservasActivity.this, "¡Reserva confirmada con éxito!", Toast.LENGTH_LONG).show();
                    finish(); // Cerramos y volvemos al dashboard
                } else {
                    btnSiguiente.setEnabled(true);
                    btnSiguiente.setText("¡CONFIRMAR CITA!");
                    Log.e("RESERVA_ERROR", "Código: " + response.code());
                    Toast.makeText(ReservasActivity.this, "Error al agendar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSiguiente.setEnabled(true);
                btnSiguiente.setText("¡CONFIRMAR CITA!");
                Toast.makeText(ReservasActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}