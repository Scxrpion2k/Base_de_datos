package com.example.base_de_datos.Controlador.EstadisticaJuego;

public class EstadisticaJuegoItem {

    private String idJuego;
    private String idEstadistica;
    private String idEquipo;
    private String idJugador;
    private int cantidad;

    public EstadisticaJuegoItem(String idJuego, String idEstadistica, String idEquipo, String idJugador, int cantidad) {
        this.idJuego = idJuego;
        this.idEstadistica = idEstadistica;
        this.idEquipo = idEquipo;
        this.idJugador = idJugador;
        this.cantidad = cantidad;
    }

    public String getIdJuego() {
        return idJuego;
    }

    public String getIdEstadistica() {
        return idEstadistica;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public String getIdJugador() {
        return idJugador;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() {
        // Devuelve una representación legible para ComboBox o debugging
        return "Juego: " + idJuego + ", Estadística: " + idEstadistica +
                ", Equipo: " + idEquipo + ", Jugador: " + idJugador + ", Cantidad: " + cantidad;
    }
}
