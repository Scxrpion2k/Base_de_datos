package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.*;

public class EstadisticaJuegoReporte {

    @FXML private AnchorPane rootReporte;
    @FXML private Label lblTitulo;
    @FXML private TableView<String[]> tablaReporte;

    @FXML private TableColumn<String[], String> col1;
    @FXML private TableColumn<String[], String> col2;
    @FXML private TableColumn<String[], String> col3;
    @FXML private TableColumn<String[], String> col4;
    @FXML private TableColumn<String[], String> col5;
    @FXML private TableColumn<String[], String> col6;
    @FXML private TableColumn<String[], String> col7;
    @FXML private TableColumn<String[], String> col8;

    public void cargarReporte(String idJuego) {

        lblTitulo.setText("Reporte del Juego " + idJuego);

        col1.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        col2.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        col3.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        col4.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        col5.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        col6.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));
        col7.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[6]));
        col8.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[7]));

        tablaReporte.getItems().clear();
        ejecutarSP(idJuego);
    }

    private void ejecutarSP(String idJuego) {

        String sql = "{CALL tabJueg(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, idJuego);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                String[] fila = new String[8];
                for (int i = 0; i < 8; i++) {
                    fila[i] = rs.getString(i + 1);
                }
                tablaReporte.getItems().add(fila);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), rootReporte);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(ev -> {
            StackPane content = (StackPane) rootReporte.getParent();
            content.getChildren().remove(rootReporte);
        });

        fade.play();
    }
}
