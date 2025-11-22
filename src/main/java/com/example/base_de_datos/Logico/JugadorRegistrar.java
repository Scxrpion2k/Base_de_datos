package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JugadorRegistrar {

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombreJugador;
    @FXML private ComboBox<CiudadItem> cmbCiudadNacimiento;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNumeroJugador;
    @FXML private ComboBox<EquipoItem> cmbEquipo;

    @FXML
    public void initialize() {
        cargarCiudades();
        cargarEquipos();
    }

    private void cargarCiudades() {
        ObservableList<CiudadItem> listaCiudades = FXCollections.observableArrayList();
        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT idCiudad, nombreCiudad FROM Ciudad")) {

            while (rs.next()) {
                listaCiudades.add(new CiudadItem(rs.getString("idCiudad"), rs.getString("nombreCiudad")));
            }
            cmbCiudadNacimiento.setItems(listaCiudades);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEquipos() {
        ObservableList<EquipoItem> listaEquipos = FXCollections.observableArrayList();

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                     "SELECT idEquipo, nombreEquipo, idCiudad FROM Equipo")) {

            while (rs.next()) {
                listaEquipos.add(new EquipoItem(
                        rs.getString("idEquipo"),
                        rs.getString("nombreEquipo"),
                        rs.getString("idCiudad")
                ));
            }

            cmbEquipo.setItems(listaEquipos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    @FXML
    private void guardarJugador() {
        String id = txtIdJugador.getText();
        String nombre = txtNombreJugador.getText();
        CiudadItem ciudad = cmbCiudadNacimiento.getValue();
        EquipoItem equipo = cmbEquipo.getValue();
        String numero = txtNumeroJugador.getText();
        String fecha = dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : null;

        if (id.isEmpty() || nombre.isEmpty() || ciudad == null || equipo == null || numero.isEmpty() || fecha == null) {
            showAlert("Todos los campos son obligatorios.");
            return;
        }

        try (Connection con = Conexion.getConnection()) {
            String sql = "INSERT INTO Jugador (idJugador, nombreJugador, idCiudadNacimiento, fechaNacimiento, numeroJugador, idEquipo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, ciudad.getId());
            ps.setString(4, fecha);
            ps.setString(5, numero);
            ps.setString(6, equipo.getId());

            ps.executeUpdate();
            showAlert("Jugador registrado correctamente.");
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al registrar jugador.");
        }
    }

    @FXML
    private void limpiar() {
        txtIdJugador.clear();
        txtNombreJugador.clear();
        cmbCiudadNacimiento.getSelectionModel().clearSelection();
        cmbEquipo.getSelectionModel().clearSelection();
        dpFechaNacimiento.setValue(null);
        txtNumeroJugador.clear();
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.show();
    }

    public void volverAlMenuPrincipal() {
        try {
            BorderPane root = (BorderPane) txtIdJugador.getScene().getRoot();

            StackPane content = (StackPane) root.getCenter();

            content.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
