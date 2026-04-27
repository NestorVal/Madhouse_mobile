package com.madhouse.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ImageView btnMenuHamburguesa;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Enlazar variables con el XML
        btnMenuHamburguesa = findViewById(R.id.btnMenuHamburguesa);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Iniciar con el fragmento de "Inicio" por defecto
        if (savedInstanceState == null) {
            cambiarFragmento(new InicioFragment());
        }

        // Lógica del Menú Hamburguesa (Popup)
        btnMenuHamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMenuHamburguesa(v);
            }
        });

        // Lógica del Bottom Navigation Bar (Pestañas inferiores)
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.item_inicio) {
                cambiarFragmento(new InicioFragment());
                return true;
            } else if (id == R.id.item_servicios) {
                cambiarFragmento(new ServiciosFragment());
                return true;
            } else if (id == R.id.item_barberos) {
                cambiarFragmento(new BarberosFragment());
                return true;
            }
            return false;
        });
    }

    // Función auxiliar para desplegar el menú de usuario (PopupMenu)
    private void mostrarMenuHamburguesa(View vista) {
        PopupMenu popup = new PopupMenu(this, vista);
        popup.getMenuInflater().inflate(R.menu.menu_hamburguesa, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                // === LÓGICA DEL DASHBOARD SEGÚN EL ROL ===
                if (id == R.id.nav_mi_perfil) {

                    // 1. Leemos el rol guardado en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MadhousePrefs", MODE_PRIVATE);
                    String rolUsuario = prefs.getString("rol", "ROLE_CLIENTE"); // Cliente por defecto

                    // 2. Elegimos la pantalla correcta
                    Intent intent;
                    if (rolUsuario.equals("ROLE_BARBERO")) {
                        intent = new Intent(HomeActivity.this, DashboardBarberoActivity.class);
                    } else {
                        intent = new Intent(HomeActivity.this, DashboardClienteActivity.class);
                    }

                    // 3. Viajamos al Dashboard
                    startActivity(intent);
                    return true;

                    // === LÓGICA DE CERRAR SESIÓN ===
                } else if (id == R.id.nav_cerrar_sesion) {

                    // Borramos los datos de sesión para que no pueda entrar sin contraseña
                    SharedPreferences prefs = getSharedPreferences("MadhousePrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Toast.makeText(HomeActivity.this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    // Borramos el historial de pantallas
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    // Método para cambiar fragmentos en el contenedor principal
    private void cambiarFragmento(androidx.fragment.app.Fragment fragmentoNuevo) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_principal, fragmentoNuevo)
                .commit();
    }
}