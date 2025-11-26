package com.example.base_de_datos.Controlador.Jugador;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Ciudad.CiudadItem;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class JugadorRegistrar {

    @FXML private AnchorPane rootRegistrar;

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombreJugador;
    @FXML private TextField txtNumeroJugador;
    @FXML private ComboBox<CiudadItem> cmbCiudadNacimiento;
    @FXML private ComboBox<EquipoItem> cmbEquipo;
    @FXML private DatePicker dpFechaNacimiento;
    private JugadorListar jugadorListarController;

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
    public void guardarJugador() {
        try {
            String id = txtIdJugador.getText();
            String nombre = txtNombreJugador.getText();
            String numero = txtNumeroJugador.getText();
            CiudadItem ciudadSeleccionada = cmbCiudadNacimiento.getValue();
            EquipoItem equipoSeleccionado = cmbEquipo.getValue();

            if (id.isEmpty() || nombre.isEmpty() || numero.isEmpty()
                    || ciudadSeleccionada == null || equipoSeleccionado == null
                    || dpFechaNacimiento.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Campos incompletos");
                alert.setHeaderText(null);
                alert.setContentText("Todos los campos son obligatorios.");
                alert.showAndWait();
                return;
            }

            java.sql.Date fechaNacimiento = java.sql.Date.valueOf(dpFechaNacimiento.getValue());

            String sql = "INSERT INTO Jugador (idJugador, nombreJugador, idciudadNacimiento, fechaNacimiento, numeroJugador, idEquipo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, id);
                ps.setString(2, nombre);
                ps.setString(3, ciudadSeleccionada.getId());
                ps.setDate(4, fechaNacimiento);
                ps.setString(5, numero);
                ps.setString(6, equipoSeleccionado.getId());

                ps.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Jugador Guardado");
                alert.setHeaderText(null);
                alert.setContentText("El jugador ha sido guardado correctamente.");
                alert.showAndWait();

                limpiar();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el jugador");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void setJugadorListarController(JugadorListar controller) {
        this.jugadorListarController = controller;
    }

    @FXML
    public void limpiar() {
        txtIdJugador.clear();
        txtNombreJugador.clear();
        txtNumeroJugador.clear();
        cmbCiudadNacimiento.getSelectionModel().clearSelection();
        cmbEquipo.getSelectionModel().clearSelection();
        dpFechaNacimiento.setValue(null);
    }

    @FXML
    public void cerrarFormulario() {
        try {
            AnchorPane modal = rootRegistrar;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), modal);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(ev -> {
                parent.getChildren().remove(modal);


                if (jugadorListarController != null) return;

                com.example.base_de_datos.PaginaPrincipal.volverAlDashboard();
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
