package com.example.base_de_datos.Controlador.Ciudad;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CiudadRegistrar {

    @FXML
    private TextField txtIdCiudad;
    @FXML
    private TextField txtNombreCiudad;

    @FXML
    private AnchorPane rootRegistrar;
    private CiudadListar ciudadListarController;

    @FXML
    private void guardarCiudad() {

        String id = txtIdCiudad.getText().trim();
        String nombre = txtNombreCiudad.getText().trim();

        if (id.isEmpty() || nombre.isEmpty()) {
            mostrar("Todos los campos son obligatorios.");
            return;
        }

        String sql = "INSERT INTO Ciudad (idCiudad, nombreCiudad) VALUES (?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.executeUpdate();

            mostrar("Ciudad registrada correctamente.");
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al registrar la ciudad. Verifica que el ID no exista.");
        }
    }

    private void mostrar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.show();
    }

    @FXML
    private void limpiar() {
        txtIdCiudad.clear();
        txtNombreCiudad.clear();
    }

    public void setCiudadListarController(CiudadListar controller) {
        this.ciudadListarController = controller;
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


                if (ciudadListarController != null) {
                    ciudadListarController.cargarCiudades();
                } else {

                    PaginaPrincipal.volverAlDashboard();
                }
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
