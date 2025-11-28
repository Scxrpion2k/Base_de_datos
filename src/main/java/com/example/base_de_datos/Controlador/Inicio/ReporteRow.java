package com.example.base_de_datos.Controlador.Inicio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReporteRow {

    private final StringProperty[] values;

    public ReporteRow(int columnas) {

        // Usa indexación NORMAL: 0 .. columnas-1
        values = new StringProperty[columnas];

        for (int i = 0; i < columnas; i++) {
            values[i] = new SimpleStringProperty("");
        }
    }

    public void set(int index, String value) {
        if (index - 1 >= 0 && index - 1 < values.length) {
            values[index - 1].set(value);
        }
    }

    public StringProperty get(int index) {
        if (index - 1 >= 0 && index - 1 < values.length) {
            return values[index - 1];
        }
        return new SimpleStringProperty("");
    }
}
