package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Controlador.Inicio.ReporteRow;
import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.*;

public class EstadisticaJuegoReporte {

    @FXML
    private AnchorPane rootReporte;
    @FXML
    private TableView<ReporteRow> tablaReporte;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblFecha;

    public void cargarReporte(String idJuego) {

        lblTitulo.setText("Reporte del Juego " + idJuego);

        tablaReporte.getColumns().clear();
        tablaReporte.getItems().clear();

        try (Connection con = Conexion.getConnection()) {

            CallableStatement cs = con.prepareCall("{call sp_EstadisticasPorJuego(?)}");
            cs.setString(1, idJuego);

            ResultSet rs = cs.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            int columnas = md.getColumnCount();


            for (int i = 1; i <= columnas; i++) {
                final int colIndex = i;

                TableColumn<ReporteRow, String> col =
                        new TableColumn<>("");

                col.setCellValueFactory(data -> data.getValue().get(colIndex));

                tablaReporte.getColumns().add(col);
            }


            while (rs.next()) {
                ReporteRow row = new ReporteRow(columnas);

                for (int i = 1; i <= columnas; i++) {
                    row.set(i, rs.getString(i) == null ? "" : rs.getString(i));
                }

                tablaReporte.getItems().add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), rootReporte);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane parent = (StackPane) rootReporte.getParent();
            parent.getChildren().remove(rootReporte);
            PaginaPrincipal.volverAlDashboard();
        });

        fade.play();
    }
}
