package com.madhouse.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private EditText etNombre, etApellido, etCorreo, etTelefono, etDireccion, etFecha, etEspecialidad, etBio;
    private LinearLayout panelBarbero;
    private Button btnGuardar;
    private String base64Imagen = "";
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarComponentes();

        // 1. Obtener datos de sesión
        SharedPreferences prefs = getSharedPreferences("MadhousePrefs", MODE_PRIVATE);
        idUsuario = prefs.getInt("idUsuario", 0);
        String rol = prefs.getString("rol", "");

        // 2. Mostrar campos si es barbero
        if (rol.equals("ROLE_BARBERO")) {
            panelBarbero.setVisibility(View.VISIBLE);
        }

        // 3. Cargar datos actuales desde el servidor
        cargarDatosServidor();

        // 4. Configurar eventos
        findViewById(R.id.fabSeleccionarFoto).setOnClickListener(v -> abrirGaleria());
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        btnGuardar.setOnClickListener(v -> actualizarPerfil());
        findViewById(R.id.btnVolverPerfil).setOnClickListener(v -> finish());
    }

    private void inicializarComponentes() {
        imgPerfil = findViewById(R.id.imgPerfil);
        etNombre = findViewById(R.id.etNombrePerfil);
        etApellido = findViewById(R.id.etApellidoPerfil);
        etCorreo = findViewById(R.id.etCorreoPerfil);
        etTelefono = findViewById(R.id.etTelefonoPerfil);
        etDireccion = findViewById(R.id.etDireccionPerfil);
        etFecha = findViewById(R.id.etFechaPerfil);
        etEspecialidad = findViewById(R.id.etEspecialidadPerfil);
        etBio = findViewById(R.id.etBioPerfil);
        panelBarbero = findViewById(R.id.panelBarbero);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
    }

    private void cargarDatosServidor() {
        RetrofitClient.getApiService().obtenerUsuarioPorId(idUsuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body();
                    etNombre.setText(u.getNombre());
                    etApellido.setText(u.getApellido());
                    etCorreo.setText(u.getCorreo());
                    etTelefono.setText(u.getTelefono());
                    etDireccion.setText(u.getDireccion());
                    etFecha.setText(u.getFechaNacimiento());
                    etEspecialidad.setText(u.getEspecialidad());
                    etBio.setText(u.getBiografia());

                    if (u.getFoto() != null && !u.getFoto().isEmpty()) {
                        decodificarYMostrarFoto(u.getFoto());
                        base64Imagen = u.getFoto();
                    }
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherGaleria.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgPerfil.setImageBitmap(bitmap);
                        base64Imagen = convertirBitmapABase64(bitmap);
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
    );

    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void decodificarYMostrarFoto(String base64) {
        try {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgPerfil.setImageBitmap(decodedByte);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            etFecha.setText(y + "-" + String.format("%02d", (m + 1)) + "-" + String.format("%02d", d));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarPerfil() {
        Usuario u = new Usuario();
        u.setNombre(etNombre.getText().toString());
        u.setApellido(etApellido.getText().toString());
        u.setTelefono(etTelefono.getText().toString());
        u.setDireccion(etDireccion.getText().toString());
        u.setFechaNacimiento(etFecha.getText().toString());
        u.setEspecialidad(etEspecialidad.getText().toString());
        u.setBiografia(etBio.getText().toString());
        u.setFoto(base64Imagen);

        RetrofitClient.getApiService().actualizarUsuario(idUsuario, u).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PerfilActivity.this, "¡Perfil Actualizado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}