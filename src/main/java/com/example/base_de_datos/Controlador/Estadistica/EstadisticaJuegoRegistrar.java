package com.example.base_de_datos.Controlador.Estadistica;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class EstadisticaJuegoRegistrar {

    @FXML private TextField txtIdJuego;
    @FXML private ComboBox<String> cmbEstadistica;
    @FXML private ComboBox<String> cmbEquipo;
    @FXML private ComboBox<String> cmbJugador;
    @FXML private TextField txtCantidad;

    private String idJuego;

    public void setIdJuego(String idJuego) {
        this.idJuego = idJuego;
        txtIdJuego.setText(idJuego);
        cargarEstadisticas();
        cargarEquipos();
    }

    private void cargarEstadisticas() {
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombreEstadistica FROM Estadistica")) {

            ObservableList<String> lista = FXCollections.observableArrayList();
            while (rs.next()) {
                lista.add(rs.getString("nombreEstadistica"));
            }
            cmbEstadistica.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEquipos() {
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT e.nombreEquipo " +
                             "FROM Equipo e " +
                             "JOIN Juego j ON e.idEquipo = j.idEquipoLocal OR e.idEquipo = j.idEquipoVisitante " +
                             "WHERE j.idJuego = '" + idJuego + "'")) {

            ObservableList<String> equipos = FXCollections.observableArrayList();
            while (rs.next()) {
                equipos.add(rs.getString("nombreEquipo"));
            }
            cmbEquipo.setItems(equipos);

            cmbEquipo.setOnAction(ev -> cargarJugadores());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarJugadores() {
        String equipo = cmbEquipo.getValue();
        if (equipo == null) return;

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT j.nombreJugador " +
                             "FROM Jugador j " +
                             "JOIN JugadorJuego jj ON j.idJugador = jj.idJugador " +
                             "JOIN Equipo e ON j.idEquipo = e.idEquipo " +
                             "WHERE e.nombreEquipo = '" + equipo + "' AND jj.idJuego = '" + idJuego + "'")) {

            ObservableList<String> jugadores = FXCollections.observableArrayList();
            while (rs.next()) {
                jugadores.add(rs.getString("nombreJugador"));
            }
            cmbJugador.setItems(jugadores);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void guardarEstadistica(ActionEvent actionEvent) {
        String estadistica = cmbEstadistica.getValue();
        String equipo = cmbEquipo.getValue();
        String jugador = cmbJugador.getValue();
        String cantidad = txtCantidad.getText();

        if (estadistica == null || equipo == null || jugador == null || cantidad.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debe completar todos los campos.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = Conexion.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO EstadisticaJuego(idJuego, idEstadistica, idEquipo, idJugador, cantidad) " +
                             "VALUES (?, (SELECT idEstadistica FROM Estadistica WHERE nombreEstadistica=?), " +
                             "(SELECT idEquipo FROM Equipo WHERE nombreEquipo=?), " +
                             "(SELECT idJugador FROM Jugador WHERE nombreJugador=? AND idEquipo=(SELECT idEquipo FROM Equipo WHERE nombreEquipo=?)), ?)")) {

            ps.setString(1, idJuego);
            ps.setString(2, estadistica);
            ps.setString(3, equipo);
            ps.setString(4, jugador);
            ps.setString(5, equipo); // asegurar que toma el jugador del equipo correcto
            ps.setInt(6, Integer.parseInt(cantidad));

            ps.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estadística registrada correctamente.");
            alert.showAndWait();
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al registrar la estadística: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void limpiar() {
        cmbEstadistica.getSelectionModel().clearSelection();
        cmbEquipo.getSelectionModel().clearSelection();
        cmbJugador.getItems().clear();
        txtCantidad.clear();
    }

    @FXML
    public void cerrarVentana(ActionEvent actionEvent) {
        Stage stage = (Stage) txtIdJuego.getScene().getWindow();
        stage.close();
    }
}







