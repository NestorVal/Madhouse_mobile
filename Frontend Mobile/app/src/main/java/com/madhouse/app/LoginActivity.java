package com.madhouse.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Declarar las variables que representarán a los elementos de la pantalla
    private EditText etCorreo;
    private EditText etContrasena;
    private Button btnIngresar;
    private TextView tvRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aca le indicamos a Java qué diseño XML debe pintar en la pantalla
        setContentView(R.layout.activity_login);

        // Enlazamos las variables de Java con los IDs del XML
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistrarse = findViewById(R.id.tvRegistrarse);

        // Creamos el evento onClick para el botón
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejarEnvio();
            }
        });

        // Configuramos el evento para que al tocar el texto, nos lleve al Registro
        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos la intención de ir de esta pantalla (LoginActivity) a la de Registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    };

    // La función que se ejecuta al darle click
    private void manejarEnvio() {
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Creamos un objeto Usuario solo con los datos de inicio de sesión
        Usuario credenciales = new Usuario();
        credenciales.setCorreo(correo);
        credenciales.setContrasena(contrasena);

        Toast.makeText(this, "Verificando credenciales...", Toast.LENGTH_SHORT).show();

        // 2. Enviamos el objeto a Spring Boot
        RetrofitClient.getApiService().login(credenciales).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Usuario usuarioLogueado = response.body();
                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + usuarioLogueado.getNombre() + "!", Toast.LENGTH_SHORT).show();

                    // --- SOLUCIÓN: GUARDAMOS LOS DATOS AQUÍ, ANTES DE VIAJAR ---
                    android.content.SharedPreferences prefs = getSharedPreferences("MadhousePrefs", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = prefs.edit();

                    // ¡AHORA SÍ METEMOS LOS DATOS AL BAÚL!
                    editor.putInt("idUsuario", usuarioLogueado.getIdUsuario());
                    editor.putString("nombre", usuarioLogueado.getNombre());
                    editor.putString("rol", usuarioLogueado.getRol());
                    editor.apply();
                    // -----------------------------------------------------------

                    // 3. Viajamos al Home
                    Intent intencionDeViaje = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intencionDeViaje);
                    finish(); // Destruimos el Login para que no pueda volver atrás

                } else {
                    // Error 401 (No autorizado) o 404
                    Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("API_ERROR", "Fallo el Login: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error de conexión con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }
}