package com.example.base_de_datos.Controlador.Equipo;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Ciudad.CiudadItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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
    private AnchorPane rootRegistrar;
    private EquipoListar equipoListarController;


    @FXML
    public void initialize() {
        cargarCiudades();
    }

    public void setEquipoListarController(EquipoListar controller) {
        this.equipoListarController = controller;
    }

    private void cargarCiudades() {
        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT idCiudad, nombreCiudad FROM Ciudad")) {

            while (rs.next()) {
                cmbCiudad.getItems().add(
                        new CiudadItem(
                                rs.getString("idCiudad"),
                                rs.getString("nombreCiudad")
                        )
                );
            }

        } catch (Exception e) {
            System.out.println("ERROR en cargarCiudades:");
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarEquipo() {

        String id = txtIdEquipo.getText().trim();
        String nombre = txtNombreEquipo.getText().trim();
        CiudadItem ciudad = cmbCiudad.getValue();

        if (id.isEmpty() || nombre.isEmpty() || ciudad == null) {
            showAlert("Todos los campos son obligatorios.");
            return;
        }

        try (Connection con = Conexion.getConnection()) {

            String sql = "INSERT INTO Equipo (idEquipo, nombreEquipo, idCiudad) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, ciudad.getId());
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
            AnchorPane modal = rootRegistrar;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(ev -> {
                parent.getChildren().remove(modal);

                if (equipoListarController != null) return;


                com.example.base_de_datos.PaginaPrincipal.volverAlDashboard();
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}




