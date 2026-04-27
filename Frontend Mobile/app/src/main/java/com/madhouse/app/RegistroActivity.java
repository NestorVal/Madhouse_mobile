package com.madhouse.app;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;

import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etCorreo, etPass, etDireccion, etTel, etFecha;
    private Spinner spRol;
    private Button btnRegistrar;
    private TextView tvIrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // 1. Enlazar componentes
        inicializarComponentes();

        // 2. Configurar el Selector de Rol (Spinner)
        String[] opciones = {"Soy Cliente", "Soy Barbero"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spRol.setAdapter(adapter);

        // 3. Evento para abrir el calendario
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        // 4. Evento del botón registrar
        btnRegistrar.setOnClickListener(v -> ejecutarRegistro());

        // 5. Volver al login
        tvIrLogin.setOnClickListener(v -> finish());
    }

    private void inicializarComponentes() {
        etNombre = findViewById(R.id.etRegNombre);
        etApellido = findViewById(R.id.etRegApellido);
        etCorreo = findViewById(R.id.etRegCorreo);
        etPass = findViewById(R.id.etRegPassword);
        etDireccion = findViewById(R.id.etRegDireccion);
        etTel = findViewById(R.id.etRegTelefono);
        etFecha = findViewById(R.id.etRegFecha);
        spRol = findViewById(R.id.spRegRol);
        btnRegistrar = findViewById(R.id.btnCrearCuenta);
        tvIrLogin = findViewById(R.id.tvVolverLogin);
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Formato que espera Spring Boot (YYYY-MM-DD)
            String fechaSeleccionada = year1 + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
            etFecha.setText(fechaSeleccionada);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void ejecutarRegistro() {
        // Validar campos vacíos
        if (etNombre.getText().toString().isEmpty() || etCorreo.getText().toString().isEmpty() || etPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(etNombre.getText().toString());
        nuevo.setApellido(etApellido.getText().toString());
        nuevo.setCorreo(etCorreo.getText().toString());
        nuevo.setContrasena(etPass.getText().toString());
        nuevo.setDireccion(etDireccion.getText().toString());
        nuevo.setTelefono(etTel.getText().toString());
        nuevo.setFechaNacimiento(etFecha.getText().toString());

        // Mapear selección del Spinner al Rol que entiende tu Back
        String rolSeleccionado = spRol.getSelectedItem().toString().equals("Soy Cliente") ? "ROLE_CLIENTE" : "ROLE_BARBERO";
        nuevo.setRol(rolSeleccionado);

        // Llamada a Retrofit
        RetrofitClient.getApiService().registrarUsuario(nuevo).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "¡Registro Exitoso!", Toast.LENGTH_LONG).show();
                    finish(); // Regresa al Login
                } else {
                    Toast.makeText(RegistroActivity.this, "Error: El correo ya podría estar registrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}