package com.madhouse.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 1. Inflamos el diseño y lo guardamos en la variable 'vista'
        View vista = inflater.inflate(R.layout.fragment_inicio, container, false);

        // 2. Buscamos el botón usando la variable 'vista' que acabamos de crear
        // NOTA: Asegúrate de que en tu fragment_inicio.xml el ID sea exactamente btnReservarCita
        Button btnReservarCita = vista.findViewById(R.id.btnReservarCita);

        if (btnReservarCita != null) {
            btnReservarCita.setOnClickListener(v -> {
                // Mensaje de feedback
                Toast.makeText(getContext(), "Abriendo selección de servicios...", Toast.LENGTH_SHORT).show();

                // Intent para saltar a la ReservasActivity
                Intent intent = new Intent(getActivity(), ReservasActivity.class);
                startActivity(intent);

                // Efecto de transición
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // 3. Retornamos la vista ya configurada
        return vista;
    }
}