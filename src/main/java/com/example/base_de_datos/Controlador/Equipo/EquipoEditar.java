package com.example.base_de_datos.Controlador.Equipo;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoEditar {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbCiudad;
    @FXML
    private AnchorPane rootEditar;

    private String idOriginal;
    private EquipoListar equipoListarController;

    @FXML
    public void initialize() {
        cargarCiudades();
    }

    public void setEquipoListarController(EquipoListar controller) {
        this.equipoListarController = controller;
    }

    private void cargarCiudades() {
        cmbCiudad.getItems().clear();

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT nombre_ciudad FROM Ciudad")) {

            while (rs.next()) {
                cmbCiudad.getItems().add(rs.getString("nombre_ciudad"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarEquipo(EquipoItem item) {
        idOriginal = item.getId();
        txtId.setText(item.getId());
        txtNombre.setText(item.getNombre());
        cmbCiudad.setValue(item.getCiudad());
        txtId.setEditable(false);
    }

    @FXML
    public void guardarCambios() {

        String query = """
                UPDATE Equipo
                SET nombre_equipo = ?, idciudad =
                    (SELECT idCiudad FROM Ciudad WHERE nombre_ciudad = ?)
                WHERE idequipo = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, cmbCiudad.getValue());
            ps.setString(3, idOriginal);

            ps.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Actualizado con éxito.").show();

            if (equipoListarController != null) {
                equipoListarController.cargarEquiposAsync();
            }

            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al actualizar.").show();
        }
    }

    @FXML
    public void cerrarVentana() {
        FadeTransition fade = new FadeTransition(Duration.millis(200), rootEditar);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane parent = (StackPane) rootEditar.getParent();
            parent.getChildren().remove(rootEditar);

            if (equipoListarController != null) {
                equipoListarController.cargarEquiposAsync();
            }
        });

        fade.play();
    }
}