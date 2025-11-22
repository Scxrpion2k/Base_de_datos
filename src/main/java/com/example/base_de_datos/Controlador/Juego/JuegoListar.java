package com.example.base_de_datos.Controlador.Juego;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JuegoListar {

    @FXML private TableView<JuegoItem> tablaJuegos;
    @FXML private TableColumn<JuegoItem, String> colIdJuego;
    @FXML private TableColumn<JuegoItem, String> colDescripcion;
    @FXML private TableColumn<JuegoItem, String> colEquipoA;
    @FXML private TableColumn<JuegoItem, String> colEquipoB;
    @FXML private TableColumn<JuegoItem, String> colFecha;
    @FXML private TableColumn<JuegoItem, Void> colAcciones;

    @FXML private Button btnRegistrar;
    @FXML private Button btnCerrar;

    private final ObservableList<JuegoItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tablaJuegos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colIdJuego.setCellValueFactory(new PropertyValueFactory<>("idJuego"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEquipoA.setCellValueFactory(new PropertyValueFactory<>("equipoA"));
        colEquipoB.setCellValueFactory(new PropertyValueFactory<>("equipoB"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        btnRegistrar.setOnAction(e -> abrirFormularioRegistro());
        btnCerrar.setOnAction(e -> volverAlMenuPrincipal());

        centrarColumnas();
        agregarBotones();

        cargarJuegosAsync();
    }

    private void cargarJuegosAsync() {

        Task<ObservableList<JuegoItem>> task = new Task<>() {
            @Override
            protected ObservableList<JuegoItem> call() throws Exception {

                ObservableList<JuegoItem> temp = FXCollections.observableArrayList();

                String query = """
                        SELECT 
                            j.idJuego,
                            j.descripcionJuego,
                            A.nombreEquipo AS equipoA,
                            B.nombreEquipo AS equipoB,
                            j.fechaJuego
                        FROM Juego j
                        INNER JOIN Equipo A ON j.idEquipoA = A.idEquipo
                        INNER JOIN Equipo B ON j.idEquipoB = B.idEquipo
                        ORDER BY j.fechaJuego DESC
                        """;

                try (Connection con = Conexion.getConnection();
                     ResultSet rs = con.createStatement().executeQuery(query)) {

                    while (rs.next()) {
                        temp.add(new JuegoItem(
                                rs.getString("idJuego"),
                                rs.getString("descripcionJuego"),
                                rs.getString("equipoA"),
                                rs.getString("equipoB"),
                                rs.getString("fechaJuego")
                        ));
                    }
                }

                return temp;
            }
        };

        task.setOnSucceeded(e -> {
            lista.setAll(task.getValue());
            tablaJuegos.setItems(lista);
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void agregarBotones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnUpdate = new Button("Actualizar");
            private final Button btnDelete = new Button("Eliminar");
            private final HBox contenedor = new HBox(10);

            {
                contenedor.setAlignment(Pos.CENTER);

                btnUpdate.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");

                btnUpdate.setOnMouseEntered(ev -> btnUpdate.setStyle(
                        "-fx-background-color: #0b5ed7; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                ));
                btnUpdate.setOnMouseExited(ev -> btnUpdate.setStyle(
                        "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                ));

                btnDelete.setOnMouseEntered(ev -> btnDelete.setStyle(
                        "-fx-background-color: #bb2d3b; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                ));
                btnDelete.setOnMouseExited(ev -> btnDelete.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                ));

                contenedor.getChildren().addAll(btnUpdate, btnDelete);

                btnDelete.setOnAction(e -> {
                    JuegoItem item = getTableView().getItems().get(getIndex());
                    eliminarJuego(item.getIdJuego());
                });

                btnUpdate.setOnAction(e -> {
                    JuegoItem item = getTableView().getItems().get(getIndex());
                    abrirVentanaActualizar(item);
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void eliminarJuego(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Juego");
        alert.setContentText("¿Desea eliminar el juego con ID " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            String query = "DELETE FROM Juego WHERE idJuego = ?";

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, id);
                ps.executeUpdate();

                cargarJuegosAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void centrarColumnas() {
        colIdJuego.setStyle("-fx-alignment: CENTER;");
        colDescripcion.setStyle("-fx-alignment: CENTER;");
        colEquipoA.setStyle("-fx-alignment: CENTER;");
        colEquipoB.setStyle("-fx-alignment: CENTER;");
        colFecha.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
    }

    private void abrirVentanaActualizar(JuegoItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Juego/JuegoEditarVisual.fxml"));
            Parent modal = loader.load();

            JuegoEditar controller = loader.getController();
            controller.cargarJuego(item);

            BorderPane root = (BorderPane) tablaJuegos.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            modal.setOpacity(0);
            content.getChildren().add(modal);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), modal);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void volverAlMenuPrincipal() {
        try {
            BorderPane root = (BorderPane) tablaJuegos.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();
            content.getChildren().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Juego/JuegoRegistrarVisual.fxml"));
            Parent modal = loader.load();

            modal.setId("modalRegistrarJuego");

            BorderPane root = (BorderPane) tablaJuegos.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            content.getChildren().removeIf(node ->
                    "modalRegistrarJuego".equals(node.getId())
            );

            modal.setOpacity(0);
            content.getChildren().add(modal);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), modal);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
