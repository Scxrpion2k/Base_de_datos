package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JugadorListar {

    @FXML private TableView<JugadorItem> tablaJugadores;
    @FXML private TableColumn<JugadorItem, String> colId;
    @FXML private TableColumn<JugadorItem, String> colNombre;
    @FXML private TableColumn<JugadorItem, String> colCiudad;
    @FXML private TableColumn<JugadorItem, String> colFecha;
    @FXML private TableColumn<JugadorItem, String> colNumero;
    @FXML private TableColumn<JugadorItem, String> colEquipo;
    @FXML private TableColumn<JugadorItem, Void> colAcciones;

    private ObservableList<JugadorItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tablaJugadores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudadNacimiento"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));

        cargarJugadores();
        agregarBotones();
    }

    private void cargarJugadores() {
        lista.clear();

        String query = """
            SELECT j.idJugador, j.nombreJugador, c.nombreCiudad, j.fechaNacimiento, j.numeroJugador, e.nombreEquipo
            FROM Jugador j
            INNER JOIN Ciudad c ON j.idCiudadNacimiento = c.idCiudad
            INNER JOIN Equipo e ON j.idEquipo = e.idEquipo
        """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                lista.add(new JugadorItem(
                        rs.getString("idJugador"),
                        rs.getString("nombreJugador"),
                        rs.getString("nombreCiudad"),
                        rs.getString("fechaNacimiento"),
                        rs.getString("numeroJugador"),
                        rs.getString("nombreEquipo")
                ));
            }

            tablaJugadores.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarBotones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnUpdate = new Button("Actualizar");
            private final Button btnDelete = new Button("Eliminar");
            private final HBox contenedor = new HBox(10, btnUpdate, btnDelete);

            {
                btnUpdate.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");

                btnDelete.setOnAction(e -> {
                    JugadorItem item = getTableView().getItems().get(getIndex());
                    eliminarJugador(item.getId());
                });

                btnUpdate.setOnAction(e -> {
                    JugadorItem item = getTableView().getItems().get(getIndex());
                    abrirVentanaActualizar(item);
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                if (empty) setGraphic(null);
                else setGraphic(contenedor);
            }
        });
    }

    private void eliminarJugador(String id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar el jugador " + id + "?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Jugador");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String query = "DELETE FROM Jugador WHERE idJugador = ?";
            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                ps.executeUpdate();
                cargarJugadores();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirVentanaActualizar(JugadorItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/JugadorEditarVisual.fxml"));
            Parent root = loader.load();

            JugadorEditar controller = loader.getController();
            controller.cargarJugador(item);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Actualizar Jugador");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volverAlMenuPrincipal() {
        BorderPane root = (BorderPane) tablaJugadores.getScene().getRoot();
        StackPane content = (StackPane) root.getCenter();
        content.getChildren().clear();
    }
}
