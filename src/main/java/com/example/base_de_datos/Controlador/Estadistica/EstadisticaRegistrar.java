package com.example.base_de_datos.Controlador.Estadistica;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Juego.JuegoListar;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EstadisticaRegistrar {

    @FXML private TextField txtIdEstadistica;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtValor;
    @FXML private AnchorPane rootRegistrar;
    private EstadisticaListar EstadisticaListarController;

    public void setEstadisticaListarController(EstadisticaListar controller) {
        this.EstadisticaListarController = controller;
    }

    @FXML
    private void guardar() {
        try (Connection con = Conexion.getConnection()) {

            String sql = "INSERT INTO Estadistica (idEstadistica, descripcionEstadistica, valor) VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtIdEstadistica.getText().trim());
            ps.setString(2, txtDescripcion.getText().trim());
            ps.setInt(3, Integer.parseInt(txtValor.getText().trim()));

            ps.executeUpdate();

            mostrarMensaje("Estadística registrada correctamente");
            if(EstadisticaListarController != null) {
                EstadisticaListarController.cargarEstadisticas();
            }

            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError(e.getMessage());
        }

    }

    @FXML
    private void limpiar() {
        txtIdEstadistica.clear();
        txtDescripcion.clear();
        txtValor.clear();
    }

    @FXML
    public void cerrarFormulario() {
        try {
            AnchorPane modal = rootRegistrar;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> {
                parent.getChildren().remove(modal);

                if (EstadisticaListarController != null) {
                    EstadisticaListarController.cargarEstadisticas();
                } else {
                    PaginaPrincipal.volverAlDashboard();
                }
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
