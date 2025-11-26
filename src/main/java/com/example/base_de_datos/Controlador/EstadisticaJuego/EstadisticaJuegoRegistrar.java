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


    public void setJuego(JuegoItem juego) {
        this.juego = juego;

        txtIdJuego.setText(juego.getIdJuego());
        cargarEquipos();
        cargarEstadisticas();
    }

    private void cargarEquipos() {
        cmbEquipo.getItems().clear();

        String sql = """
                SELECT idEquipo, nombreEquipo
                FROM Equipo
                WHERE nombreEquipo = ? OR nombreEquipo = ?
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, juego.getEquipoA());
            ps.setString(2, juego.getEquipoB());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbEquipo.getItems().add(
                        new EquipoItem(
                                rs.getString("idEquipo"),
                                rs.getString("nombreEquipo")
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
                SELECT j.idJugador, j.nombreJugador
                FROM Jugador j
                WHERE j.idEquipo = ?
                ORDER BY j.nombreJugador
                """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbJugador.getItems().add(
                        rs.getString("idJugador") + " - " + rs.getString("nombreJugador")
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void cargarEstadisticas() {
        cmbEstadistica.getItems().clear();

        String sql = """
                SELECT idEstadistica, descripcionEstadistica, valor
                FROM Estadistica
                ORDER BY descripcionEstadistica
                """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                cmbEstadistica.getItems().add(
                        rs.getString("idEstadistica") +
                                " - " + rs.getString("descripcionEstadistica") +
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
                INSERT INTO EstadisticaDeJuego(idJuego, idEstadisticaRegistrar, idEquipo, idJugador, cantidad)
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
    public void cerrar() {
        try {
            AnchorPane modal = rootRegistrar;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> {
                PaginaPrincipal.volverAlDashboard();
                modal.setVisible(false);
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
