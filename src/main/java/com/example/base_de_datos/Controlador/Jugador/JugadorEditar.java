package com.example.base_de_datos.Controlador.Jugador;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Ciudad.CiudadItem;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class JugadorEditar {

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<CiudadItem> cmbCiudad;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtNumero;
    @FXML private ComboBox<EquipoItem> cmbEquipo;
    @FXML private AnchorPane rootEditarJugador;

    private JugadorListar jugadorListarController;
    private String idOriginal;

    @FXML
    public void initialize() {
        cargarCiudades();
        cargarEquipos();
    }

    private void cargarCiudades() {
        cmbCiudad.getItems().clear();
        String sql = "SELECT idCiudad, nombreCiudad FROM Ciudad";

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                cmbCiudad.getItems().add(new CiudadItem(
                        rs.getString("idCiudad"),
                        rs.getString("nombreCiudad")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEquipos() {
        cmbEquipo.getItems().clear();
        String sql = """
                SELECT e.idEquipo, e.nombreEquipo, c.nombreCiudad
                FROM Equipo e
                INNER JOIN Ciudad c ON e.idCiudad = c.idCiudad
                """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                cmbEquipo.getItems().add(new EquipoItem(
                        rs.getString("idEquipo"),
                        rs.getString("nombreEquipo"),
                        rs.getString("nombreCiudad")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarJugador(JugadorItem item) {

        idOriginal = item.getId();

        txtIdJugador.setText(item.getId());
        txtNombre.setText(item.getNombre());

        cmbCiudad.getItems().stream()
                .filter(x -> x.getNombre().equals(item.getCiudadNacimiento()))
                .findFirst()
                .ifPresent(cmbCiudad::setValue);

        if (item.getFechaNacimiento() != null && item.getFechaNacimiento().length() >= 10) {
            dpFecha.setValue(LocalDate.parse(item.getFechaNacimiento().substring(0, 10)));
        }

        txtNumero.setText(item.getNumero());

        cmbEquipo.getItems().stream()
                .filter(x -> x.getNombre().equals(item.getEquipo()))
                .findFirst()
                .ifPresent(cmbEquipo::setValue);

        txtIdJugador.setEditable(false);
    }

    @FXML
    public void guardarCambios() {

        if (txtNombre.getText().isEmpty()
                || cmbCiudad.getValue() == null
                || dpFecha.getValue() == null
                || txtNumero.getText().isEmpty()
                || cmbEquipo.getValue() == null) {

            mostrar("Todos los campos son obligatorios.");
            return;
        }

        String sql = """
                UPDATE Jugador
                SET nombreJugador = ?, idCiudadNacimiento = ?, 
                    fechaNacimiento = ?, numeroJugador = ?, idEquipo = ?
                WHERE idJugador = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, cmbCiudad.getValue().getId());
            ps.setString(3, dpFecha.getValue().toString());
            ps.setString(4, txtNumero.getText());
            ps.setString(5, cmbEquipo.getValue().getId());
            ps.setString(6, idOriginal);

            ps.executeUpdate();

            mostrar("Jugador actualizado con éxito.");

            if (jugadorListarController != null) {
                jugadorListarController.cargarJugadoresAsync();
            }

            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al actualizar el jugador.");
        }
    }

    @FXML
    public void cerrarVentana() {
        try {
            AnchorPane modal = rootEditarJugador;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fade = new FadeTransition(Duration.millis(180), modal);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(ev -> {
                parent.getChildren().remove(modal);
                if (jugadorListarController != null) {
                    jugadorListarController.cargarJugadoresAsync();
                }
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setJugadorListarController(JugadorListar controller) {
        this.jugadorListarController = controller;
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.show();
    }
}
