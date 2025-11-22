package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CiudadEditar {

    @FXML private TextField txtIdCiudad;
    @FXML private TextField txtNombreCiudad;

    private String idOriginal;

    // Referencia al controlador de la tabla
    private CiudadListar ciudadListarController;

    public void setCiudadListarController(CiudadListar controller) {
        this.ciudadListarController = controller;
    }

    public void cargarCiudad(CiudadItem item) {
        idOriginal = item.getId();
        txtIdCiudad.setText(item.getId());
        txtNombreCiudad.setText(item.getNombre());
    }

    @FXML
    public void guardarCambios() {

        String query = """
            UPDATE Ciudad
            SET idCiudad = ?, nombreCiudad = ?
            WHERE idCiudad = ?
        """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, txtIdCiudad.getText());
            ps.setString(2, txtNombreCiudad.getText());
            ps.setString(3, idOriginal);

            ps.executeUpdate();

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Ciudad actualizada con éxito.");
            ok.show();

            // REFRESCAR TABLA
            if (ciudadListarController != null) {
                ciudadListarController.cargarCiudades();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Error al actualizar la ciudad.");
            error.show();
        }
    }
}
