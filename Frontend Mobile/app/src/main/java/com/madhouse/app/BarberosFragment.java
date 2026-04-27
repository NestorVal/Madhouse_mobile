package com.madhouse.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.madhouse.app.models.Usuario;
import com.madhouse.app.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarberosFragment extends Fragment {

    private RecyclerView recyclerView;
    private BarberoAdapter adaptador; // Lo declaramos global

    public BarberosFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_barberos, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewBarberos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // SOLUCIÓN AL ERROR: Le ponemos un adaptador vacío INMEDIATAMENTE
        adaptador = new BarberoAdapter(new ArrayList<>());
        recyclerView.setAdapter(adaptador);

        // Ahora sí, mandamos a pedir los datos a Spring Boot
        cargarBarberosDesdeBackend();

        return vista;
    }

    private void cargarBarberosDesdeBackend() {
        RetrofitClient.getApiService().obtenerBarberos().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> barberos = response.body();
                    // Usamos el nuevo método para inyectar los datos reales
                    adaptador.actualizarDatos(barberos);
                } else {
                    // Si llega aquí, significa que la ruta en Spring Boot no existe (Error 404)
                    Log.e("API_ERROR", "Código de error del servidor: " + response.code());
                    Toast.makeText(getContext(), "Error: Revisa que el endpoint en Spring Boot exista", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("API_ERROR", "Fallo la conexión: " + t.getMessage());
            }
        });
    }
}