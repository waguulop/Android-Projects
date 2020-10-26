package com.example.calculadora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class ActivityEscogerColor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escoger_color);
    }

    public void cambiaFondoColorBlanco(View view){
    }

    public void cambiaFondoColorAmarillo(View view){
        Intent cambiarAColorAmarillo = new Intent(this, MainActivity.class);
        cambiarAColorAmarillo.putExtra("Amarillo", "Color.YELLOW");
        startActivity(cambiarAColorAmarillo);
    }

    public void cambiaFondoColorVerde(View view){
    }

}