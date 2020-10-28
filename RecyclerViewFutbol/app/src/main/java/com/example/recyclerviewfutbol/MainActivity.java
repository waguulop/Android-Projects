package com.example.recyclerviewfutbol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEquipo;
    private RecyclerViewAdaptador adaptadorEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerViewEquipo = findViewById(R.id.recyclerEquipos);
        this.recyclerViewEquipo.setLayoutManager(new LinearLayoutManager(this));

        adaptadorEquipo = new RecyclerViewAdaptador(generarEquipos());
        recyclerViewEquipo.setAdapter(adaptadorEquipo);
    }

    public ArrayList<Equipo> generarEquipos(){
        ArrayList<Equipo> equiposGenerados = new ArrayList<>();
        equiposGenerados.add(new Equipo("Real Madrid", "Zinedine Zidane", "Santiago Bernabeu", R.drawable.realmadrid));
        equiposGenerados.add(new Equipo("FC Barcelona", "Ronald Koeman", "Camp Nou", R.drawable.barcelona));
        return equiposGenerados;
    }
}