package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CiudadRegistrar {

    @FXML private TextField txtIdCiudad;
    @FXML private TextField txtNombreCiudad;

    @FXML
    private void guardarCiudad() {

        String id = txtIdCiudad.getText().trim();
        String nombre = txtNombreCiudad.getText().trim();

        if (id.isEmpty() || nombre.isEmpty()) {
            showAlert("Todos los campos son obligatorios.");
            return;
        }

        String sql = "INSERT INTO Ciudad (idCiudad, nombreCiudad) VALUES (?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                showAlert("No se puede conectar a la base de datos.");
                return;
            }

            ps.setString(1, id);
            ps.setString(2, nombre);

            ps.executeUpdate();

            showAlert("Ciudad registrada correctamente.");
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al registrar la ciudad. Verifica que el ID no exista.");
        }
    }

    @FXML
    private void limpiar() {
        txtIdCiudad.clear();
        txtNombreCiudad.clear();
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.show();
    }

    public void volverAlMenuPrincipal() {
        try {
            BorderPane root = (BorderPane) txtIdCiudad.getScene().getRoot();

            StackPane content = (StackPane) root.getCenter();

            content.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
