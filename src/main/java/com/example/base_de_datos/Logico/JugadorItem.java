package com.example.base_de_datos.Logico;

public class JugadorItem {
    private String id;
    private String nombre;
    private String ciudadNacimiento;
    private String fechaNacimiento;
    private String numero;
    private String equipo;

    public JugadorItem(String id, String nombre, String ciudadNacimiento, String fechaNacimiento, String numero, String equipo) {
        this.id = id;
        this.nombre = nombre;
        this.ciudadNacimiento = ciudadNacimiento;
        this.fechaNacimiento = fechaNacimiento;
        this.numero = numero;
        this.equipo = equipo;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCiudadNacimiento() { return ciudadNacimiento; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getNumero() { return numero; }
    public String getEquipo() { return equipo; }

    @Override
    public String toString() {
        return nombre;
    }
}
