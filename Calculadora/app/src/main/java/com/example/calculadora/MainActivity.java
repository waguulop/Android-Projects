package com.example.calculadora;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout layoutPrincipal;
    TextView textoNumeros;
    TextView resultadoView;
    double num = 0;
    double resultado = 0;
    Boolean sumando = false, restando = false, multiplicando = false, dividiendo = false, porcentaje = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton botonEscogerColor = findViewById(R.id.imgBotonEscogerColor);
        resultadoView = findViewById(R.id.textViewResultado);
        this.layoutPrincipal = (ConstraintLayout) findViewById(R.id.PantallaPrincipal);
        this.textoNumeros = (TextView) findViewById(R.id.textNumeros);
    }

    public void botonReiniciar(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Cuidado!");
        alertDialog.setMessage("Vas a reiniciar tu cuenta");
        alertDialog.show();
        this.num = 0;
        this.resultado = 0;
        this.resultadoView.setText("");
        this.textoNumeros.setText("");
    }

    public void print0(View view){
        textoNumeros.setText(textoNumeros.getText() + "0");
    }

    public void print1(View view){
        textoNumeros.setText(textoNumeros.getText() + "1");
    }

    public void print2(View view){
        textoNumeros.setText(textoNumeros.getText() + "2");
    }

    public void print3(View view){
        textoNumeros.setText(textoNumeros.getText() + "3");
    }

    public void print4(View view){
        textoNumeros.setText(textoNumeros.getText() + "4");
    }

    public void print5(View view){
        textoNumeros.setText(textoNumeros.getText() + "5");
    }

    public void print6(View view){
        textoNumeros.setText(textoNumeros.getText() + "6");
    }

    public void print7(View view){
        textoNumeros.setText(textoNumeros.getText() + "7");
    }

    public void print8(View view){
        textoNumeros.setText(textoNumeros.getText() + "8");
    }

    public void print9(View view) {
        textoNumeros.setText(textoNumeros.getText() + "9");
    }

    public void printPunto(View view){
        textoNumeros.setText(textoNumeros.getText() + ".");
    }

    public void obtenerNumero(){
        CharSequence num1 = this.textoNumeros.getText();
        this.num = Double.parseDouble(num1.toString());
    }

    public void botonSumar(View view){
        obtenerNumero();
        this.resultado += this.num;
        this.textoNumeros.setText("");
        this.resultadoView.setText(Double.toString(this.resultado));
        this.sumando = true;
    }

    public void botonRestar(View view){
        obtenerNumero();
        if (this.restando == false)
            this.resultado -= this.num;
        this.textoNumeros.setText("");
        this.resultadoView.setText(Double.toString(this.resultado));
        this.restando = true;
    }

    public void botonMultiplicar(View view){
        obtenerNumero();
        if (this.resultado == 0)
            this.resultado = this.num;
        else
            this.resultado *= this.num;
        this.textoNumeros.setText("");
        this.resultadoView.setText(Double.toString(this.resultado));
        this.multiplicando = true;
    }

    public void botonDividir(View view){
        obtenerNumero();
        if (this.resultado == 0)
            this.resultado = this.num;
        else
            this.resultado /= this.num;

        this.textoNumeros.setText("");
        this.resultadoView.setText(Double.toString(this.resultado));
        this.dividiendo = true;
    }

    public void botonPorcentaje(View view){
        obtenerNumero();
        this.resultado %= this.num;
        this.textoNumeros.setText("");
        this.resultadoView.setText(Double.toString(this.resultado));
        this.porcentaje = true;
    }

    public void mostrarResultado(View view){
        if (this.sumando == true)
            botonSumar(view);
        if (this.restando == true)
            botonRestar(view);
        if (this.multiplicando == true)
            botonMultiplicar(view);
        if (this.dividiendo == true)
            botonDividir(view);
            if (this.num == 0){
                Toast aviso = Toast.makeText(this, "No se puede dividir entre 0", Toast.LENGTH_SHORT);
                aviso.show();
            }
        if (this.porcentaje == true)
            botonPorcentaje(view);
        String resultado = Double.toString(this.resultado);
        this.textoNumeros.setText(resultado);
        this.restaurarEstados();
    }

    public void restaurarEstados(){
        this.sumando = false; this.restando = false; this.multiplicando = false; this.dividiendo = false; this.porcentaje = false;
    }

    public void filtrarDecimalCero(){
        
    }

    public void actividadEscogerColor(View view){
        Intent iniciarCambioDeColor = new Intent(this, ActivityEscogerColor.class);
        startActivity(iniciarCambioDeColor);
    }
}