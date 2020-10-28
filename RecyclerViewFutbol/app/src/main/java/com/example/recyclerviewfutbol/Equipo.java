package com.example.recyclerviewfutbol;

public class Equipo {

    private String nombre_equipo;
    private String entrenador;
    private String estadio;
    private int logoEquipo;

    public Equipo() {
    }

    public Equipo(String nombre_equipo, String entrenador, String estadio, int logoEquipo) {
        this.nombre_equipo = nombre_equipo;
        this.entrenador = entrenador;
        this.estadio = estadio;
        this.logoEquipo = logoEquipo;
    }

    public String getNombre_equipo() {
        return nombre_equipo;
    }

    public void setNombre_equipo(String nombre_equipo) {
        this.nombre_equipo = nombre_equipo;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(String entrenador) {
        this.entrenador = entrenador;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public int getLogoEquipo() {
        return logoEquipo;
    }

    public void setLogoEquipo(int logoEquipo) {
        this.logoEquipo = logoEquipo;
    }
}
