package com.example.base_de_datos.Controlador.Jugador;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Ciudad.CiudadItem;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JugadorRegistrar extends Application {

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombreJugador;
    @FXML private TextField txtNumeroJugador;
    @FXML private ComboBox<CiudadItem> cmbCiudadNacimiento;
    @FXML private ComboBox<EquipoItem> cmbEquipo;
    @FXML private DatePicker dpFechaNacimiento;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Visual/JugadorRegistrarVisual.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Registrar Jugador");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cargarCiudades();
        cargarEquipos();
    }

    private void cargarCiudades() {
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idCiudad, nombreCiudad FROM Ciudad")) {

            while (rs.next()) {
                cmbCiudadNacimiento.getItems().add(
                        new CiudadItem(rs.getString("idCiudad"), rs.getString("nombreCiudad"))
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEquipos() {
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idEquipo, nombreEquipo, idCiudad FROM Equipo")) {

            while (rs.next()) {
                cmbEquipo.getItems().add(
                        new EquipoItem(
                                rs.getString("idEquipo"),
                                rs.getString("nombreEquipo"),
                                rs.getString("idCiudad")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void volverAlMenuPrincipal(ActionEvent actionEvent) {
        try {
            BorderPane root = (BorderPane) txtIdJugador.getScene().getRoot();

            StackPane content = (StackPane) root.getCenter();

            content.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void guardarJugador(ActionEvent actionEvent) {
        String id = txtIdJugador.getText();
        String nombre = txtNombreJugador.getText();
        String numero = txtNumeroJugador.getText();
        CiudadItem ciudadSeleccionada = cmbCiudadNacimiento.getValue();
        EquipoItem equipoSeleccionado = cmbEquipo.getValue();
        java.sql.Date fechaNacimiento = java.sql.Date.valueOf(dpFechaNacimiento.getValue());

        if (ciudadSeleccionada == null || equipoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar una ciudad y un equipo.");
            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO Jugador (idJugador, nombreJugador, idciudadNacimiento, fechaNacimiento, numeroJugador, idEquipo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, ciudadSeleccionada.getId()); // usar String
            ps.setDate(4, fechaNacimiento);
            ps.setString(5, numero);
            ps.setString(6, equipoSeleccionado.getId()); // usar String

            ps.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Jugador Guardado");
            alert.setHeaderText(null);
            alert.setContentText("El jugador ha sido guardado correctamente.");
            alert.showAndWait();

            limpiar(actionEvent);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el jugador");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void limpiar(ActionEvent actionEvent) {
        txtIdJugador.clear();
        txtNombreJugador.clear();
        txtNumeroJugador.clear();
        cmbCiudadNacimiento.getSelectionModel().clearSelection();
        cmbEquipo.getSelectionModel().clearSelection();
        dpFechaNacimiento.setValue(null);
    }
}
