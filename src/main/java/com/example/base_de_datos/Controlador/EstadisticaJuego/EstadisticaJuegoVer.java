package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Juego.JuegoItem;
import com.example.base_de_datos.PaginaPrincipal;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EstadisticaJuegoVer {

    @FXML private AnchorPane rootEstadisticas;

    @FXML private Label lblJuego;
    @FXML private Label lblEquipos;

    @FXML private TableView<EstadisticaJuegoItem> tablaEstadisticas;
    @FXML private TableColumn<EstadisticaJuegoItem, String> colEquipo;
    @FXML private TableColumn<EstadisticaJuegoItem, String> colJugador;
    @FXML private TableColumn<EstadisticaJuegoItem, String> colEstadistica;
    @FXML private TableColumn<EstadisticaJuegoItem, Integer> colCantidad;
    @FXML private TableColumn<EstadisticaJuegoItem, Void> colAcciones;

    private String idJuegoActual;
    private final ObservableList<EstadisticaJuegoItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tablaEstadisticas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));
        colJugador.setCellValueFactory(new PropertyValueFactory<>("jugador"));
        colEstadistica.setCellValueFactory(new PropertyValueFactory<>("estadistica"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        agregarBotonesAcciones();
    }

    public void cargarJuego(JuegoItem juego) {

        idJuegoActual = juego.getIdJuego();

        lblJuego.setText("Juego: " + juego.getIdJuego());
        lblEquipos.setText(juego.getEquipoA() + " vs " + juego.getEquipoB());

        cargarEstadisticas(idJuegoActual);
    }

    private void cargarEstadisticas(String idJuego) {

        lista.clear();

        String sql = """
            SELECT 
                ej.idEquipo,
                ej.idJugador,
                ej.idEstadisticaRegistrar,
                e.nombreEquipo,
                j.nombreJugador,
                es.descripcionEstadistica,
                ej.cantidad,
                CASE 
                    WHEN ej.idEquipo = (SELECT idEquipoA FROM Juego WHERE idJuego = ?) THEN 1
                    ELSE 2
                END AS ordenEquipo
            FROM EstadisticaDeJuego ej
            INNER JOIN Equipo e ON ej.idEquipo = e.idEquipo
            INNER JOIN Jugador j ON ej.idJugador = j.idJugador
            INNER JOIN Estadistica es ON ej.idEstadisticaRegistrar = es.idEstadistica
            WHERE ej.idJuego = ?
            ORDER BY ordenEquipo, e.nombreEquipo, j.nombreJugador, es.descripcionEstadistica
            """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idJuego);
            ps.setString(2, idJuego);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(new EstadisticaJuegoItem(
                        rs.getString("nombreEquipo"),
                        rs.getString("nombreJugador"),
                        rs.getString("descripcionEstadistica"),
                        rs.getInt("cantidad"),
                        rs.getString("idEquipo"),
                        rs.getString("idJugador"),
                        rs.getString("idEstadisticaRegistrar")
                ));
            }

            tablaEstadisticas.setItems(lista);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void agregarBotonesAcciones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox cont = new HBox(8);

            {
                btnEditar.setStyle("-fx-background-color:#0d6efd; -fx-text-fill:white; -fx-background-radius:8;");
                btnEliminar.setStyle("-fx-background-color:#dc3545; -fx-text-fill:white; -fx-background-radius:8;");

                cont.setAlignment(Pos.CENTER);
                cont.getChildren().addAll(btnEditar, btnEliminar);

                btnEditar.setOnAction(e -> abrirEditar(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : cont);
            }
        });
    }

    private void abrirEditar(EstadisticaJuegoItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Visual/EstadisticaJuego/EstadisticaJuegoEditarVisual.fxml"));

            Parent modal = loader.load();

            EstadisticaJuegoEditar controller = loader.getController();
            controller.cargarDatos(idJuegoActual, item);

            StackPane content = (StackPane) rootEstadisticas.getParent();
            modal.setOpacity(0);
            content.getChildren().add(modal);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void eliminar(EstadisticaJuegoItem item) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Eliminar Estadística");
        alert.setContentText("¿Seguro deseas eliminar esta estadística?");

        if (alert.showAndWait().get() != ButtonType.OK) return;

        String sql = """
            DELETE FROM EstadisticaDeJuego
            WHERE idJuego = ? AND idEquipo = ? AND idJugador = ? AND idEstadisticaRegistrar = ?
            """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idJuegoActual);
            ps.setString(2, item.getIdEquipo());
            ps.setString(3, item.getIdJugador());
            ps.setString(4, item.getIdEstadistica());

            ps.executeUpdate();
            cargarEstadisticas(idJuegoActual);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {
        FadeTransition fade = new FadeTransition(Duration.millis(200), rootEstadisticas);
        fade.setFromValue(1);
        fade.setToValue(0);

        fade.setOnFinished(e -> {
            StackPane parent = (StackPane) rootEstadisticas.getParent();
            parent.getChildren().remove(rootEstadisticas);
            PaginaPrincipal.volverAlDashboard();  // 🔥 volver al inicio
        });

        fade.play();
    }


}
