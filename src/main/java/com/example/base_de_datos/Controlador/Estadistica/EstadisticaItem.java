package com.example.base_de_datos.Controlador.Estadistica;

public class EstadisticaItem {

    private String idEstadistica;
    private String descripcionEstadistica;
    private int valor;

    public EstadisticaItem(String idEstadistica, String descripcionEstadistica, int valor) {
        this.idEstadistica = idEstadistica;
        this.descripcionEstadistica = descripcionEstadistica;
        this.valor = valor;
    }

    public String getIdEstadistica() {
        return idEstadistica;
    }

    public String getDescripcionEstadistica() {
        return descripcionEstadistica;
    }

    public int getValor() {
        return valor;
    }
}
