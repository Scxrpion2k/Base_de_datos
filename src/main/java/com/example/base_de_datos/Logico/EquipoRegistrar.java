package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoRegistrar {

    @FXML
    private TextField txtIdEquipo;
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private ComboBox<CiudadItem> cmbCiudad;

    @FXML
    public void initialize() {
        cargarCiudades();
    }

    private void cargarCiudades() {

        try {
            Connection con = Conexion.getConnection();

            if (con == null) {
                System.out.println("ERROR: getConnection() devolvió null");
                return;
            }

            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT idCiudad, nombreCiudad FROM Ciudad"
            );

            boolean hayCiudades = false;

            while (rs.next()) {
                hayCiudades = true;

                String id = rs.getString("idCiudad");
                String nombre = rs.getString("nombreCiudad");


                cmbCiudad.getItems().add(new CiudadItem(id, nombre));
            }

            if (!hayCiudades) {
                System.out.println("NO HAY CIUDADES EN LA TABLA");
            }

        } catch (Exception e) {
            System.out.println("ERROR en cargarCiudades:");
            e.printStackTrace();
        }
    }


    @FXML
    private void guardarEquipo() {

        String id = txtIdEquipo.getText();
        String nombre = txtNombreEquipo.getText();
        CiudadItem ciudad = cmbCiudad.getValue();

        if (id.isEmpty() || nombre.isEmpty() || ciudad == null) {
            showAlert("Todos los campos son obligatorios.");
            return;
        }

        String ciudadId = ciudad.getId();

        try {
            Connection con = Conexion.getConnection();

            String sql = "INSERT INTO Equipo (idEquipo, nombreEquipo, idCiudad) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, ciudadId);

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

    @FXML
    public void volverAlMenuPrincipal() {
        try {
            BorderPane root = (BorderPane) txtIdEquipo.getScene().getRoot();

            StackPane content = (StackPane) root.getCenter();

            content.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
