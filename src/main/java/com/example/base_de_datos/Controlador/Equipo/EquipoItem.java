package com.example.base_de_datos.Controlador.Equipo;

public class EquipoItem {

    private String id;
    private String nombre;
    private String ciudad;

    // Constructor usado al registrar estadísticas
    public EquipoItem(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = null;
    }

    // Constructor completo para cuando cargues equipos reales
    public EquipoItem(String id, String nombre, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }

    @Override
    public String toString() {
        return nombre; // lo que se ve en el ComboBox
    }
}
