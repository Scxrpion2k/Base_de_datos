package com.example.base_de_datos.Controlador.Estadistica;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EstadisticaEditar {

    @FXML private TextField txtIdEstadistica;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtValor;
    @FXML private AnchorPane rootEditar;

    private String idOriginal;
    private EstadisticaListar EstadisticaListarController;

    public void setEstadisticaListarController(EstadisticaListar controller) {
        this.EstadisticaListarController = controller;
    }

    public void cargarDatos(EstadisticaItem estadistica) {
        idOriginal = estadistica.getIdEstadistica();
        txtIdEstadistica.setText(estadistica.getIdEstadistica());
        txtDescripcion.setText(estadistica.getDescripcionEstadistica());
        txtValor.setText(String.valueOf(estadistica.getValor()));
        txtIdEstadistica.setDisable(true);
    }

    @FXML
    private void guardarCambios() {
        try (Connection con = Conexion.getConnection()) {

            String sql = "UPDATE Estadistica SET descripcionEstadistica=?, valor=? WHERE idEstadistica=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtDescripcion.getText().trim());
            ps.setInt(2, Integer.parseInt(txtValor.getText().trim()));
            ps.setString(3, idOriginal);

            ps.executeUpdate();

            mostrarMensaje("Estadística actualizada correctamente");
            if(EstadisticaListarController != null){
                EstadisticaListarController.cargarEstadisticas();
            }
            cerrarVentana();

        } catch (Exception e) {
            mostrarError("Error al editar estadística");
        }
    }

    @FXML
    private void cerrarVentana() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), rootEditar);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> rootEditar.setVisible(false));
        ft.play();
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
