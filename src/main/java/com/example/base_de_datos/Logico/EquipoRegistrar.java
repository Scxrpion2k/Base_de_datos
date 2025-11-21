package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoRegistrar {

    @FXML private TextField txtIdEquipo;
    @FXML private TextField txtNombreEquipo;
    @FXML private ComboBox<String> cmbCiudad;

    @FXML
    public void initialize() {
        cargarCiudades();
    }

    private void cargarCiudades() {
        try {
            Connection con = Conexion.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT nombre_ciudad FROM Ciudad");

            while (rs.next()) {
                cmbCiudad.getItems().add(rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarEquipo() {
        String id = txtIdEquipo.getText();
        String nombre = txtNombreEquipo.getText();
        String ciudad = cmbCiudad.getValue();

        if (id.isEmpty() || nombre.isEmpty() || ciudad == null) {
            showAlert("Todos los campos son obligatorios.");
            return;
        }

        try {
            Connection con = Conexion.getConnection();

            String sql = "INSERT INTO Equipo (idEquipo, nombreEquipo, idCiudad) VALUES (?, ?, ?)";
            assert con != null;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, ciudad);

            ps.executeUpdate();

            showAlert("Equipo registrado correctamente.");
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al registrar equipo.");
        }
    }

    @FXML
    private void limpiar() {
        txtIdEquipo.clear();
        txtNombreEquipo.clear();
        cmbCiudad.getSelectionModel().clearSelection();
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.show();
    }
}
