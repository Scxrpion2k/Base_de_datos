package com.example.base_de_datos.Controlador.Inicio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReporteRow {

    private final StringProperty[] values;

    public ReporteRow(int columnas) {
        values = new StringProperty[columnas + 1];
        for (int i = 1; i <= columnas; i++) {
            values[i] = new SimpleStringProperty("");
        }
    }

    public void set(int index, String value) {
        values[index].set(value);
    }

    public StringProperty get(int index) {
        return values[index];
    }
}
