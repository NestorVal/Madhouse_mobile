package com.madhouse.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.madhouse.app.models.Servicio;
import com.madhouse.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearServicioActivity extends AppCompatActivity {

    private TextView btnVolver, tvDuracion;
    private TextInputEditText etNombre, etPrecio, etDescripcion;
    private Button btnMenos, btnMas, btnCrear;

    private int duracionMinutos = 0;
    private int idBarberoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_servicio);

        // 1. Obtener ID del barbero que está creando el servicio
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", Context.MODE_PRIVATE);
        idBarberoActual = prefs.getInt("idUsuario", 0);

        inicializarVistas();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnVolver = findViewById(R.id.btnVolverCrear);
        tvDuracion = findViewById(R.id.tvDuracion);
        etNombre = findViewById(R.id.etNombreServicio);
        etPrecio = findViewById(R.id.etPrecioServicio);
        etDescripcion = findViewById(R.id.etDescripcionServicio);
        btnMenos = findViewById(R.id.btnMenosTiempo);
        btnMas = findViewById(R.id.btnMasTiempo);
        btnCrear = findViewById(R.id.btnCrearServicio);
    }

    private void configurarEventos() {
        // Botón Volver
        btnVolver.setOnClickListener(v -> finish());

        // Lógica del Selector de Tiempo (Intervalos de 15 min)
        btnMenos.setOnClickListener(v -> {
            if (duracionMinutos > 0) {
                duracionMinutos -= 15;
                actualizarTextoDuracion();
            }
        });

        btnMas.setOnClickListener(v -> {
            if (duracionMinutos < 180) { // Límite de 3 horas máximo
                duracionMinutos += 15;
                actualizarTextoDuracion();
            }
        });

        // Botón Crear
        btnCrear.setOnClickListener(v -> validarYEnviarDatos());
    }

    private void actualizarTextoDuracion() {
        tvDuracion.setText(String.valueOf(duracionMinutos));
    }

    private void validarYEnviarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Validaciones Básicas
        if (nombre.isEmpty()) {
            etNombre.setError("Obligatorio");
            return;
        }
        if (duracionMinutos == 0) {
            Toast.makeText(this, "La duración no puede ser 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (precioStr.isEmpty()) {
            etPrecio.setError("Obligatorio");
            return;
        }

        double precio = Double.parseDouble(precioStr);

        // Crear el objeto para enviar
        Servicio nuevoServicio = new Servicio();
        nuevoServicio.setNombre(nombre);
        nuevoServicio.setDuracion(duracionMinutos);
        nuevoServicio.setPrecio(precio);
        nuevoServicio.setDescripcion(descripcion);
        // NOTA: Dependiendo de cómo esté tu Backend de Spring Boot,
        // podrías necesitar enviarle también el idBarberoActual para enlazarlo.

        // Llamada a la API
        btnCrear.setEnabled(false); // Desactivar botón para evitar dobles clics
        btnCrear.setText("CREANDO...");

        RetrofitClient.getApiService().crearServicio(nuevoServicio).enqueue(new Callback<Servicio>() {
            @Override
            public void onResponse(Call<Servicio> call, Response<Servicio> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrearServicioActivity.this, "¡Servicio Creado Exitosamente!", Toast.LENGTH_SHORT).show();
                    finish(); // Vuelve al Dashboard
                } else {
                    Log.e("ERROR_API", "Código: " + response.code());
                    Log.e("ERROR_API", "Cuerpo de error: " + response.errorBody().toString());
                    Toast.makeText(CrearServicioActivity.this, "Fallo: " + response.code(), Toast.LENGTH_SHORT).show();  btnCrear.setEnabled(true);
                    btnCrear.setText("CREAR SERVICIO");
                }
            }

            @Override
            public void onFailure(Call<Servicio> call, Throwable t) {
                Toast.makeText(CrearServicioActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnCrear.setEnabled(true);
                btnCrear.setText("CREAR SERVICIO");
            }
        });
    }
}