package com.example.recyclerviewfutbol;

import android.media.audiofx.DynamicsProcessing;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nombreEquipo, nombreEntrenador, nombreEstadio;
        private ImageView fotoEquipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombreEquipo = itemView.findViewById(R.id.txtNombreEquipo);
            this.nombreEntrenador = itemView.findViewById(R.id.txtNombreEntrenador);
            this.nombreEstadio = itemView.findViewById(R.id.txtNombreEstadio);
            this.fotoEquipo = itemView.findViewById(R.id.logoEquipo);
        }
    }


    public ArrayList<Equipo> listaEquipos;

    public RecyclerViewAdaptador(ArrayList<Equipo> listaEquipos) {
        this.listaEquipos = listaEquipos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipo,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombreEquipo.setText(this.listaEquipos.get(position).getNombre_equipo());
        holder.nombreEntrenador.setText(this.listaEquipos.get(position).getEntrenador());
        holder.nombreEstadio.setText(this.listaEquipos.get(position).getEstadio());
        holder.fotoEquipo.setImageResource(this.listaEquipos.get(position).getLogoEquipo());
    }

    @Override
    public int getItemCount() {
        return this.listaEquipos.size();
    }
}
