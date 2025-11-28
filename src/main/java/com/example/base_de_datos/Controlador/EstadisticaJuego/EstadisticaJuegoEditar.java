package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EstadisticaJuegoEditar {

    @FXML private AnchorPane rootEditar;
    @FXML private TextField txtEquipo;
    @FXML private TextField txtJugador;
    @FXML private TextField txtEstadistica;
    @FXML private TextField txtCantidad;

    private String idJuego, idEquipo, idJugador, idEstadistica;
    private EstadisticaJuegoVer estadisticaJuegoVerController;

    public void setEstadisticaJuegoVerController(EstadisticaJuegoVer controller) {
        this.estadisticaJuegoVerController = controller;
    }


    public void cargarDatos(String idJuego, EstadisticaJuegoItem item) {
        this.idJuego = idJuego;
        this.idEquipo = item.getIdEquipo();
        this.idJugador = item.getIdJugador();
        this.idEstadistica = item.getIdEstadistica();

        txtEquipo.setText(item.getEquipo());
        txtJugador.setText(item.getJugador());
        txtEstadistica.setText(item.getEstadistica());
        txtCantidad.setText(String.valueOf(item.getCantidad()));

        txtEquipo.setDisable(true);
        txtJugador.setDisable(true);
        txtEstadistica.setDisable(true);
    }

    @FXML
    private void guardarCambios() {

        int nuevaCantidad = Integer.parseInt(txtCantidad.getText());

        String sql = """
            UPDATE EstadisticaDeJuego
            SET cantidad = ?
            WHERE idJuego = ? AND idEquipo = ? AND idJugador = ? AND idEstadisticaRegistrar = ?
        """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevaCantidad);
            ps.setString(2, idJuego);
            ps.setString(3, idEquipo);
            ps.setString(4, idJugador);
            ps.setString(5, idEstadistica);

            ps.executeUpdate();
            if (estadisticaJuegoVerController != null) {
                estadisticaJuegoVerController.refrescarTabla();
            }
            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {

        FadeTransition fade = new FadeTransition(Duration.millis(200), rootEditar);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane content = (StackPane) rootEditar.getParent();
            content.getChildren().remove(rootEditar);
        });

        fade.play();
    }

}
