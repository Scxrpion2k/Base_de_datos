package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Juego.JuegoItem;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EstadisticaJuegoRegistrar {

    @FXML private TextField txtIdJuego;
    @FXML private ComboBox<EquipoItem> cmbEquipo;
    @FXML private ComboBox<String> cmbJugador;
    @FXML private ComboBox<String> cmbEstadistica;
    @FXML private TextField txtCantidad;
    @FXML
    private AnchorPane rootRegistrar;

    private JuegoItem juego;
    private EstadisticaJuegoVer estadisticaJuegoVerController;
    public void setEstadisticaJuegoVerController(EstadisticaJuegoVer controller) {
        this.estadisticaJuegoVerController = controller;
    }


    public void setJuego(JuegoItem juego) {
        this.juego = juego;

        txtIdJuego.setText(juego.getIdJuego());
        cargarEquipos();
        cargarEstadisticas();
    }

    private void cargarEquipos() {
        cmbEquipo.getItems().clear();

        String sql = """
                SELECT idequipo, nombre_equipo
                FROM Equipo
                WHERE nombre_equipo = ? OR nombre_equipo = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, juego.getEquipoA());
            ps.setString(2, juego.getEquipoB());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbEquipo.getItems().add(
                        new EquipoItem(
                                rs.getString("idequipo"),
                                rs.getString("nombre_equipo")
                        )
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cmbEquipo.setOnAction(e -> cargarJugadoresPorEquipo());
    }


    private void cargarJugadoresPorEquipo() {
        EquipoItem equipo = cmbEquipo.getValue();
        if (equipo == null) return;

        cmbJugador.getItems().clear();

        String sql = """
                SELECT j.idjugador, j.nombre_jugador
                FROM Jugador j
                WHERE j.idequipo = ?
                ORDER BY j.nombre_jugador
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbJugador.getItems().add(
                        rs.getString("idjugador") + " - " + rs.getString("nombre_jugador")
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void cargarEstadisticas() {
        cmbEstadistica.getItems().clear();

        String sql = """
                SELECT idestadistica, descripcion_estadistica, valor
                FROM Estadistica
                ORDER BY descripcion_estadistica
                """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                cmbEstadistica.getItems().add(
                        rs.getString("idestadistica") +
                                " - " + rs.getString("descripcion_estadistica") +
                                " (" + rs.getInt("valor") + ")"
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void limpiar() {
        cmbEquipo.getSelectionModel().clearSelection();
        cmbJugador.getItems().clear();
        cmbEstadistica.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }


    @FXML
    private void guardarEstadistica() {
        try {
            EquipoItem equipo = cmbEquipo.getValue();
            String jugador = cmbJugador.getValue();
            String estadistica = cmbEstadistica.getValue();

            if (equipo == null || jugador == null || estadistica == null) {
                mostrar("Debe completar todos los campos.");
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText());
            String idJugador = jugador.split(" - ")[0];
            String idEstadistica = estadistica.split(" - ")[0];

            String sql = """
                INSERT INTO Estadistica_Juego(idjuego, idestadistica, idequipo, idjugador, cantidad)
                VALUES (?, ?, ?, ?, ?)
                """;

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, juego.getIdJuego());
                ps.setString(2, idEstadistica);
                ps.setString(3, equipo.getId());
                ps.setString(4, idJugador);
                ps.setInt(5, cantidad);

                ps.executeUpdate();
            }

            mostrar("Estadística guardada correctamente.");

            if (estadisticaJuegoVerController != null) {
                estadisticaJuegoVerController.refrescarTabla();
            }
        } catch (Exception e) {
            mostrar("Error al guardar la estadística.");
            e.printStackTrace();
        }
    }


    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void cerrar() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), rootRegistrar);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane content = (StackPane) rootRegistrar.getParent();
            content.getChildren().remove(rootRegistrar);
        });

        fade.play();
    }
}