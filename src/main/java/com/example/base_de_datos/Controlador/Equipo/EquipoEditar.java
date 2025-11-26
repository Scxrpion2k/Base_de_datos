package com.example.base_de_datos.Controlador.Equipo;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
             ResultSet rs = con.createStatement().executeQuery("SELECT nombreCiudad FROM Ciudad")) {

            while (rs.next()) {
                cmbCiudad.getItems().add(rs.getString("nombreCiudad"));
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
    }

    @FXML
    public void guardarCambios() {

        String query = """
                    UPDATE Equipo
                    SET idEquipo = ?, nombreEquipo = ?, idCiudad =
                        (SELECT idCiudad FROM Ciudad WHERE nombreCiudad = ?)
                    WHERE idEquipo = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, txtId.getText());
            ps.setString(2, txtNombre.getText());
            ps.setString(3, cmbCiudad.getValue());
            ps.setString(4, idOriginal);

            ps.executeUpdate();

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Actualizado con éxito.");
            ok.show();

        } catch (Exception e) {
            e.printStackTrace();
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

            if (equipoListarController != null) return;

            com.example.base_de_datos.PaginaPrincipal.volverAlDashboard();
        });

        fade.play();
    }

}
