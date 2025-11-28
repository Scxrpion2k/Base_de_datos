package com.example.base_de_datos.Controlador.EstadisticaJuego;

public class EstadisticaJuegoItem {

    private String equipo;
    private String jugador;
    private String estadistica;
    private int cantidad;

    private String idEquipo;
    private String idJugador;
    private String idEstadistica;

    public EstadisticaJuegoItem(
            String equipo,
            String jugador,
            String estadistica,
            int cantidad,
            String idEquipo,
            String idJugador,
            String idEstadistica
    ) {
        this.equipo = equipo;
        this.jugador = jugador;
        this.estadistica = estadistica;
        this.cantidad = cantidad;

        this.idEquipo = idEquipo;
        this.idJugador = idJugador;
        this.idEstadistica = idEstadistica;
    }

    public String getEquipo() { return equipo; }
    public String getJugador() { return jugador; }
    public String getEstadistica() { return estadistica; }
    public int getCantidad() { return cantidad; }

    public String getIdEquipo() { return idEquipo; }
    public String getIdJugador() { return idJugador; }
    public String getIdEstadistica() { return idEstadistica; }
}
