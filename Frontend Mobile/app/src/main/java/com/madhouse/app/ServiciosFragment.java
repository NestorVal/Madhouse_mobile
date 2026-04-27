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

import com.madhouse.app.models.Servicio;
import com.madhouse.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiciosFragment extends Fragment {

    private RecyclerView recyclerView;

    public ServiciosFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_servicios, container, false);

        recyclerView = vista.findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // LLAMADA A LA API
        cargarServiciosDesdeBackend();

        return vista;
    }

    private void cargarServiciosDesdeBackend() {
        // Hacemos la petición de forma asíncrona (en segundo plano)
        RetrofitClient.getApiService().obtenerServicios().enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Si el backend responde OK (200), le pasamos la lista de JSON al Adaptador
                    List<Servicio> serviciosBD = response.body();
                    ServicioAdapter adaptador = new ServicioAdapter(serviciosBD);
                    recyclerView.setAdapter(adaptador);
                } else {
                    Toast.makeText(getContext(), "Error al cargar el catálogo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {
                // Si el servidor está apagado o no hay internet
                Log.e("API_ERROR", "Fallo la conexion: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }
}