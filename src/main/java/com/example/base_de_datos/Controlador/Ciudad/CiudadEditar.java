package com.example.base_de_datos.Controlador.Ciudad;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CiudadEditar {

    @FXML
    private TextField txtIdCiudad;
    @FXML
    private TextField txtNombreCiudad;

    @FXML
    private AnchorPane rootEditarCiudad;

    private String idOriginal;

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

        String sql = """
                    UPDATE Ciudad
                    SET idCiudad = ?, nombreCiudad = ?
                    WHERE idCiudad = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtIdCiudad.getText());
            ps.setString(2, txtNombreCiudad.getText());
            ps.setString(3, idOriginal);

            ps.executeUpdate();

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Ciudad actualizada correctamente.", ButtonType.OK);
            ok.show();

            if (ciudadListarController != null) ciudadListarController.cargarCiudades();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al actualizar los datos.").show();
        }
    }

    @FXML
    public void cerrarVentana() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), rootEditarCiudad);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane parent = (StackPane) rootEditarCiudad.getParent();

            parent.getChildren().remove(rootEditarCiudad);


            if (ciudadListarController != null) {
                ciudadListarController.cargarCiudades();
            }
        });

        fade.play();
    }

}
