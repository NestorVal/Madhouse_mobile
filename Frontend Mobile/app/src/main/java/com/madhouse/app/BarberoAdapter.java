package com.madhouse.app;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.madhouse.app.models.Usuario;
import java.util.List;

public class BarberoAdapter extends RecyclerView.Adapter<BarberoAdapter.BarberoViewHolder> {

    private List<Usuario> listaBarberos;

    public BarberoAdapter(List<Usuario> listaBarberos) {
        this.listaBarberos = listaBarberos;
    }

    //Para actualizar la lista cuando llegue la respuesta del servidor
    public void actualizarDatos(List<Usuario> nuevaLista) {
        this.listaBarberos = nuevaLista;
        notifyDataSetChanged(); // Hace que la pantalla vuelva a dibujar las tarjetas
    }

    @NonNull
    @Override
    public BarberoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barbero, parent, false);
        return new BarberoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberoViewHolder holder, int position) {
        Usuario barbero = listaBarberos.get(position);

        holder.tvNombre.setText(barbero.getNombre() + " " + barbero.getApellido());
        holder.tvEspecialidad.setText(barbero.getEspecialidad());
        holder.tvBiografia.setText(barbero.getBiografia());

        // Manejo de la imagen en Base64 con Glide
        String base64String = barbero.getFoto();
        if (base64String != null && !base64String.isEmpty()) {
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }
            byte[] imageByteArray = Base64.decode(base64String, Base64.DEFAULT);
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(imageByteArray)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.imgBarbero);
        } else {
            holder.imgBarbero.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return listaBarberos.size();
    }

    public static class BarberoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBarbero;
        TextView tvNombre, tvEspecialidad, tvBiografia;

        public BarberoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBarbero = itemView.findViewById(R.id.imgBarbero);
            tvNombre = itemView.findViewById(R.id.tvNombreBarbero);
            tvEspecialidad = itemView.findViewById(R.id.tvEspecialidad);
            tvBiografia = itemView.findViewById(R.id.tvBiografia);
        }
    }
}