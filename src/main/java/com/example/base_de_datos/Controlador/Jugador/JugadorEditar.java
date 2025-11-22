package com.example.base_de_datos.Controlador.Jugador;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Ciudad.CiudadItem;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JugadorEditar {

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombreJugador;
    @FXML private ComboBox<CiudadItem> cmbCiudadNacimiento;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNumeroJugador;
    @FXML private ComboBox<EquipoItem> cmbEquipo;

    private String idOriginal;

    @FXML
    public void initialize() {
        cargarCiudades();
        cargarEquipos();
    }

    public void cargarJugador(JugadorItem item) {
        idOriginal = item.getId();
        txtIdJugador.setText(item.getId());
        txtNombreJugador.setText(item.getNombre());
        dpFechaNacimiento.setValue(java.time.LocalDate.parse(item.getFechaNacimiento()));
        txtNumeroJugador.setText(item.getNumero());
        // Seleccionar ciudad y equipo
        cmbCiudadNacimiento.getItems().stream()
                .filter(c -> c.getId().equals(item.getCiudadNacimiento()))
                .findFirst()
                .ifPresent(c -> cmbCiudadNacimiento.setValue(c));
        cmbEquipo.getItems().stream()
                .filter(e -> e.getId().equals(item.getEquipo()))
                .findFirst()
                .ifPresent(e -> cmbEquipo.setValue(e));
    }

    private void cargarCiudades() {
        ObservableList<CiudadItem> lista = FXCollections.observableArrayList();
        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT idCiudad, nombreCiudad FROM Ciudad")) {
            while (rs.next()) {
                lista.add(new CiudadItem(rs.getString("idCiudad"), rs.getString("nombreCiudad")));
            }
            cmbCiudadNacimiento.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEquipos() {
        ObservableList<EquipoItem> lista = FXCollections.observableArrayList();
        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT idEquipo, nombreEquipo, idCiudad FROM Equipo")) {
            while (rs.next()) {
                lista.add(new EquipoItem(rs.getString("idEquipo"), rs.getString("nombreEquipo"), rs.getString("idCiudad")));
            }
            cmbEquipo.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void guardarCambios() {
        String query = """
            UPDATE Jugador
            SET idJugador = ?, nombreJugador = ?, idCiudadNacimiento = ?, fechaNacimiento = ?, numeroJugador = ?, idEquipo = ?
            WHERE idJugador = ?
        """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, txtIdJugador.getText());
            ps.setString(2, txtNombreJugador.getText());
            ps.setString(3, cmbCiudadNacimiento.getValue().getId());
            ps.setDate(4, java.sql.Date.valueOf(dpFechaNacimiento.getValue()));
            ps.setString(5, txtNumeroJugador.getText());
            ps.setString(6, cmbEquipo.getValue().getId());
            ps.setString(7, idOriginal);

            ps.executeUpdate();
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Jugador actualizado con éxito.");
            ok.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
