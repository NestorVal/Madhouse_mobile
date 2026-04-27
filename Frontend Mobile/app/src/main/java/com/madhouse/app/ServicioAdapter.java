package com.madhouse.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.madhouse.app.models.Servicio; // IMPORTAR EL MODELO
import android.widget.ImageView;
import java.util.List;
import android.util.Base64;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    // AHORA RECIBIMOS UNA LISTA DE OBJETOS REALES
    private List<Servicio> listaServicios;

    public ServicioAdapter(List<Servicio> listaServicios) {
        this.listaServicios = listaServicios;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicioActual = listaServicios.get(position);

        // Inyectar textos
        holder.tvNombre.setText(servicioActual.getNombre());
        holder.tvDescripcion.setText(servicioActual.getDescripcion());
        holder.tvPrecio.setText("$" + servicioActual.getPrecio());
        holder.tvDuracion.setText("⏱ " + servicioActual.getDuracion() + " min");

        // --- LÓGICA DE IMAGEN CON GLIDE ---
        String base64String = servicioActual.getFoto();

        // 1. Limpiamos el texto si viene de React/Web
        if (base64String != null && base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }

        // 2. Le pasamos el string crudo a Glide, él decodifica, redimensiona y dibuja automáticamente
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(Base64.decode(base64String, Base64.DEFAULT)) // Decodificamos solo los bytes
                .placeholder(R.drawable.ic_launcher_background)    // Qué mostrar mientras carga
                .error(R.drawable.ic_launcher_background)          // Qué mostrar si falla o está roto
                .centerCrop()                                      // Ajustar la foto al tamaño del cuadro
                .into(holder.imgServicio);                         // Dónde pintarlo

        // Acción del botón reservar
        holder.btnReservar.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Reservando: " + servicioActual.getNombre(), Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgServicio;
        TextView tvNombre, tvDescripcion, tvPrecio, tvDuracion; // <-- Agregamos tvDuracion
        Button btnReservar;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);

            imgServicio = itemView.findViewById(R.id.imgServicio);
            tvNombre = itemView.findViewById(R.id.tvNombreServicio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionServicio);
            tvPrecio = itemView.findViewById(R.id.tvPrecioServicio);
            tvDuracion = itemView.findViewById(R.id.tvDuracionServicio); // <-- Enlazar duración
            btnReservar = itemView.findViewById(R.id.btnReservarItem);
        }
    }
}