package com.example.base_de_datos.Controlador.Juego;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class JuegoEditar {

    @FXML private TextField txtIdJuego;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<EquipoItem> cmbEquipoA;
    @FXML private ComboBox<EquipoItem> cmbEquipoB;
    @FXML private DatePicker dpFecha;

    @FXML private AnchorPane rootEditarJuego;

    private String idOriginal;

    @FXML
    public void initialize() {
        cargarEquipos();
    }

    private void cargarEquipos() {

        cmbEquipoA.getItems().clear();
        cmbEquipoB.getItems().clear();

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                     "SELECT idEquipo, nombreEquipo, " +
                             "(SELECT nombreCiudad FROM Ciudad WHERE Ciudad.idCiudad = Equipo.idCiudad) AS ciudad " +
                             "FROM Equipo"
             )) {

            while (rs.next()) {
                EquipoItem equipo = new EquipoItem(
                        rs.getString("idEquipo"),
                        rs.getString("nombreEquipo"),
                        rs.getString("ciudad")
                );

                cmbEquipoA.getItems().add(equipo);
                cmbEquipoB.getItems().add(equipo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarJuego(JuegoItem item) {

        idOriginal = item.getIdJuego();

        txtIdJuego.setText(item.getIdJuego());
        txtDescripcion.setText(item.getDescripcion());

        cmbEquipoA.getItems().stream()
                .filter(eq -> eq.getNombre().equals(item.getEquipoA()))
                .findFirst()
                .ifPresent(cmbEquipoA::setValue);

        cmbEquipoB.getItems().stream()
                .filter(eq -> eq.getNombre().equals(item.getEquipoB()))
                .findFirst()
                .ifPresent(cmbEquipoB::setValue);

        String fecha = item.getFecha();
        if (fecha != null && fecha.length() >= 10) {
            dpFecha.setValue(LocalDate.parse(fecha.substring(0, 10)));
        }
    }

    @FXML
    public void guardarCambios() {

        if (txtIdJuego.getText().isEmpty() ||
                txtDescripcion.getText().isEmpty() ||
                cmbEquipoA.getValue() == null ||
                cmbEquipoB.getValue() == null ||
                dpFecha.getValue() == null) {

            mostrar("Todos los campos son obligatorios.");
            return;
        }

        EquipoItem a = cmbEquipoA.getValue();
        EquipoItem b = cmbEquipoB.getValue();

        if (a.getId().equals(b.getId())) {
            mostrar("Equipo A y Equipo B no pueden ser iguales.");
            return;
        }

        String sql = """
            UPDATE Juego
            SET idJuego = ?, descripcionJuego = ?, idEquipoA = ?, idEquipoB = ?, fechaJuego = ?
            WHERE idJuego = ?
        """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtIdJuego.getText());
            ps.setString(2, txtDescripcion.getText());
            ps.setString(3, a.getId());
            ps.setString(4, b.getId());
            ps.setString(5, dpFecha.getValue().toString());
            ps.setString(6, idOriginal);

            ps.executeUpdate();

            mostrar("Juego actualizado con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al actualizar el juego.");
        }
    }

    private void mostrar(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.show();
    }

    @FXML
    public void cerrarVentana() {
        try {
            BorderPane root = (BorderPane) rootEditarJuego.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            FadeTransition fade = new FadeTransition(Duration.millis(200), rootEditarJuego);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> content.getChildren().remove(rootEditarJuego));
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
