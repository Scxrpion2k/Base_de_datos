package com.example.base_de_datos.Controlador.Juego;

import com.example.base_de_datos.Conexion.Conexion;
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

public class JuegoRegistrar {

    @FXML private TextField txtIdJuego;
    @FXML private TextField txtDescripcion;

    @FXML private ComboBox<EquipoItem> cmbEquipoA;
    @FXML private ComboBox<EquipoItem> cmbEquipoB;

    @FXML private DatePicker dpFecha;
    @FXML private AnchorPane rootRegistrar;

    private JuegoListar juegoListarController;

    public void setJuegoListarController(JuegoListar controller) {
        this.juegoListarController = controller;
    }

    @FXML
    public void initialize() {
        cargarEquipos();
    }

    private void cargarEquipos() {
        String sql = "SELECT idEquipo, nombreEquipo, c.nombreCiudad " +
                "FROM Equipo e INNER JOIN Ciudad c ON e.idCiudad = c.idCiudad";

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                EquipoItem equipo = new EquipoItem(
                        rs.getString("idEquipo"),
                        rs.getString("nombreEquipo"),
                        rs.getString("nombreCiudad")
                );

                cmbEquipoA.getItems().add(equipo);
                cmbEquipoB.getItems().add(equipo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarJuego() {

        String id = txtIdJuego.getText().trim();
        String desc = txtDescripcion.getText().trim();
        EquipoItem equipoA = cmbEquipoA.getValue();
        EquipoItem equipoB = cmbEquipoB.getValue();
        String fecha = dpFecha.getValue() != null ? dpFecha.getValue().toString() : null;

        if (id.isEmpty() || desc.isEmpty() || equipoA == null || equipoB == null || fecha == null) {
            mostrar("Todos los campos son obligatorios.");
            return;
        }

        if (equipoA.getId().equals(equipoB.getId())) {
            mostrar("Equipo A y Equipo B no pueden ser iguales.");
            return;
        }

        try (Connection con = Conexion.getConnection()) {

            String sql = """
                INSERT INTO Juego (idJuego, descripcionJuego, idEquipoA, idEquipoB, fechaJuego)
                VALUES (?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, id);
            ps.setString(2, desc);
            ps.setString(3, equipoA.getId());
            ps.setString(4, equipoB.getId());
            ps.setString(5, fecha);

            ps.executeUpdate();

            mostrar("Juego registrado correctamente.");
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al registrar juego.");
        }
    }

    @FXML
    private void limpiar() {
        txtIdJuego.clear();
        txtDescripcion.clear();
        cmbEquipoA.getSelectionModel().clearSelection();
        cmbEquipoB.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
    }

    private void mostrar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.show();
    }

    @FXML
    public void cerrarFormulario() {
        try {
            AnchorPane modal = rootRegistrar;
            StackPane parent = (StackPane) modal.getParent();

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> {
                parent.getChildren().remove(modal);

                if (juegoListarController != null) return;

                PaginaPrincipal.volverAlDashboard();
            });

            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
