package com.example.base_de_datos.Controlador.Juego;

public class JuegoItem {

    private String idJuego;
    private String descripcion;
    private String equipoA;
    private String equipoB;
    private String fecha;

    public JuegoItem(String idJuego, String descripcion, String equipoA, String equipoB, String fecha) {
        this.idJuego = idJuego;
        this.descripcion = descripcion;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.fecha = fecha;
    }

    public String getIdJuego() {
        return idJuego;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEquipoA() {
        return equipoA;
    }

    public String getEquipoB() {
        return equipoB;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return idJuego;
    }
}
