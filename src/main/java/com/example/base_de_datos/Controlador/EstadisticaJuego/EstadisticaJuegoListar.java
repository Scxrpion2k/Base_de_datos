package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Juego.JuegoItem;
import com.example.base_de_datos.PaginaPrincipal;
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
import java.sql.ResultSet;

public class EstadisticaJuegoListar {

    @FXML private TableView<JuegoItem> tablaJuegos;
    @FXML private TableColumn<JuegoItem, String> colIdJuego;
    @FXML private TableColumn<JuegoItem, String> colDescripcion;
    @FXML private TableColumn<JuegoItem, String> colEquipoA;
    @FXML private TableColumn<JuegoItem, String> colEquipoB;
    @FXML private TableColumn<JuegoItem, String> colFecha;
    @FXML private TableColumn<JuegoItem, Void> colAcciones;
    @FXML private Button btnCerrar;

    private final ObservableList<JuegoItem> lista = FXCollections.observableArrayList();
    private EstadisticaJuegoVer estadisticaJuegoVerController;

    public void setEstadisticaJuegoVerController(EstadisticaJuegoVer controller) {
        this.estadisticaJuegoVerController = controller;
    }
    @FXML
    public void initialize() {

        tablaJuegos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colIdJuego.setCellValueFactory(new PropertyValueFactory<>("idJuego"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEquipoA.setCellValueFactory(new PropertyValueFactory<>("equipoA"));
        colEquipoB.setCellValueFactory(new PropertyValueFactory<>("equipoB"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        centrarColumnas();
        agregarBotones();

        btnCerrar.setOnAction(e -> volverAlMenuPrincipal());

        cargarJuegosAsync();
    }

    private void cargarJuegosAsync() {

        Task<ObservableList<JuegoItem>> task = new Task<>() {
            @Override
            protected ObservableList<JuegoItem> call() throws Exception {

                ObservableList<JuegoItem> temp = FXCollections.observableArrayList();

                String sql = """
                    SELECT j.idJuego, j.descripcionJuego,
                           A.nombreEquipo AS equipoA,
                           B.nombreEquipo AS equipoB,
                           j.fechaJuego
                    FROM Juego j
                    INNER JOIN Equipo A ON j.idEquipoA = A.idEquipo
                    INNER JOIN Equipo B ON j.idEquipoB = B.idEquipo
                    ORDER BY j.fechaJuego DESC
                    """;

                try (Connection con = Conexion.getConnection();
                     ResultSet rs = con.createStatement().executeQuery(sql)) {

                    while (rs.next()) {
                        temp.add(new JuegoItem(
                                rs.getString("idJuego"),
                                rs.getString("descripcionJuego"),
                                rs.getString("equipoA"),
                                rs.getString("equipoB"),
                                rs.getString("fechaJuego").split(" ")[0]
                        ));
                    }
                }

                return temp;
            }
        };

        task.setOnSucceeded(e -> {

            tablaJuegos.setItems(task.getValue());


            tablaJuegos.layout();

            javafx.application.Platform.runLater(() -> {
                tablaJuegos.refresh();
                tablaJuegos.layout();
            });
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }


    private void agregarBotones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnRegistrar = new Button("Registrar Estadísticas");
            private final HBox contenedor = new HBox(10);

            {
                contenedor.setAlignment(Pos.CENTER);
                btnRegistrar.setStyle("-fx-background-color:#198754; -fx-text-fill:white; -fx-background-radius:8;");

                btnRegistrar.setOnAction(e -> {
                    JuegoItem item = getTableView().getItems().get(getIndex());
                    abrirFormularioEstadistica(item);
                });

                contenedor.getChildren().add(btnRegistrar);
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void abrirFormularioEstadistica(JuegoItem juego) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Visual/EstadisticaJuego/EstadisticaJuegoRegistrarVisual.fxml"));
            Parent modal = loader.load();

            EstadisticaJuegoRegistrar controller = loader.getController();
            controller.setJuego(juego);
           // controller.setEstadisticaJuegoVerController(this);

            BorderPane root = (BorderPane) tablaJuegos.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            modal.setOpacity(0);
            content.getChildren().add(modal);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML

    public void volverAlMenuPrincipal() {
        try {
            PaginaPrincipal.volverAlDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

