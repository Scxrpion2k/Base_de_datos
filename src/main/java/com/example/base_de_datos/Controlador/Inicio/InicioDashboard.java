package com.example.base_de_datos.Controlador.Inicio;
import com.example.base_de_datos.Controlador.EstadisticaJuego.EstadisticaJuegoReporte;
import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;

public class InicioDashboard {

    @FXML
    private FlowPane panelJuegos;

    @FXML
    public void initialize() {
        cargarUltimosJuegos();
    }

    private void cargarUltimosJuegos() {

        String sql = """
            SELECT TOP 6
                j.idJuego,
                j.descripcionJuego,
                A.nombreEquipo AS equipoA,
                B.nombreEquipo AS equipoB,
                CONVERT(VARCHAR(10), j.fechaJuego, 105) AS fecha
            FROM Juego j
            INNER JOIN Equipo A ON j.idEquipoA = A.idEquipo
            INNER JOIN Equipo B ON j.idEquipoB = B.idEquipo
            ORDER BY j.fechaJuego DESC
        """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {

                String id = rs.getString("idJuego");
                String desc = rs.getString("descripcionJuego");
                String equipoA = rs.getString("equipoA");
                String equipoB = rs.getString("equipoB");
                String fecha = rs.getString("fecha");

                VBox card = crearTarjetaJuego(id, desc, equipoA, equipoB, fecha);
                panelJuegos.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox crearTarjetaJuego(String id, String desc, String equipoA, String equipoB, String fecha) {

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));

        card.setPrefSize(380, 160);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),10,0,0,3);");

        Label titulo = new Label(equipoA + " vs " + equipoB);
        titulo.setFont(Font.font(20));
        titulo.setStyle("-fx-font-weight: bold;");

        Label lblDesc = new Label(desc);
        lblDesc.setTextFill(Color.GRAY);

        Label lblFecha = new Label("Fecha: " + fecha);
        lblFecha.setTextFill(Color.BLACK);

        Button ver = new Button("Ver Detalles");
        ver.setStyle("-fx-background-color:#0d6efd; -fx-text-fill:white; -fx-background-radius:10;"
                + "-fx-padding:6 15; -fx-cursor:hand;");

        ver.setOnAction(e -> abrirReporteTabJueg(id));

        card.getChildren().addAll(titulo, lblDesc, lblFecha, ver);

        return card;
    }

    private void abrirReporteTabJueg(String idJuego) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Visual/EstadisticaJuego/EstadisticaJuegoReporteVisual.fxml"));

            Parent modal = loader.load();

            // 👇 AQUÍ está el cambio importante
            EstadisticaJuegoReporte controller = loader.getController();
            controller.cargarReporte(idJuego);   // 👈 usa el método correcto

            StackPane root = (StackPane) panelJuegos.getScene().lookup("#mainContent");

            modal.setOpacity(0);
            root.getChildren().add(modal);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
