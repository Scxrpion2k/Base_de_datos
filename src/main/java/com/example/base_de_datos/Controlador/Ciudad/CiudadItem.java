package com.example.base_de_datos.Controlador.Ciudad;

public class CiudadItem {
    private String id;
    private String nombre;

    public CiudadItem(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
