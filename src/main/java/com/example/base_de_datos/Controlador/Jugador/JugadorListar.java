package com.example.base_de_datos.Controlador.Jugador;

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

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class JugadorListar {


    @FXML private TextField txtBuscar;


    @FXML private TableView<JugadorItem> tablaJugadores;
    @FXML private TableColumn<JugadorItem, String> colId;
    @FXML private TableColumn<JugadorItem, String> colNombre;
    @FXML private TableColumn<JugadorItem, String> colCiudad;
    @FXML private TableColumn<JugadorItem, String> colFecha;
    @FXML private TableColumn<JugadorItem, String> colNumero;
    @FXML private TableColumn<JugadorItem, String> colEquipo;
    @FXML private TableColumn<JugadorItem, Void> colAcciones;


    @FXML private Button btnRegistrar;
    @FXML private Button btnCerrar;


    private final ObservableList<JugadorItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tablaJugadores.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);


        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudadNacimiento"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));

        centrarColumnas();
        agregarBotones();

        btnRegistrar.setOnAction(e -> abrirFormularioRegistro());
        btnCerrar.setOnAction(e -> volverAlMenuPrincipal());


        cargarJugadoresAsync();


        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));
    }


    private void filtrarTabla(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {
            tablaJugadores.setItems(lista);
            return;
        }

        String lower = filtro.toLowerCase();

        ObservableList<JugadorItem> filtrada = FXCollections.observableArrayList();

        for (JugadorItem j : lista) {

            if (j.getNombre().toLowerCase().contains(lower)
) {

                filtrada.add(j);
            }
        }

        tablaJugadores.setItems(filtrada);
    }

    private void centrarColumnas() {
        colId.setStyle("-fx-alignment: CENTER;");
        colNombre.setStyle("-fx-alignment: CENTER;");
        colCiudad.setStyle("-fx-alignment: CENTER;");
        colFecha.setStyle("-fx-alignment: CENTER;");
        colNumero.setStyle("-fx-alignment: CENTER;");
        colEquipo.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
    }


    public void cargarJugadoresAsync() {

        Task<ObservableList<JugadorItem>> task = new Task<>() {
            @Override
            protected ObservableList<JugadorItem> call() throws Exception {

                ObservableList<JugadorItem> temp = FXCollections.observableArrayList();

                String sql = """
                    SELECT j.idJugador, j.nombreJugador,
                           c.nombreCiudad, j.fechaNacimiento,
                           j.numeroJugador, e.nombreEquipo
                    FROM Jugador j
                    INNER JOIN Ciudad c ON j.idCiudadNacimiento = c.idCiudad
                    INNER JOIN Equipo e ON j.idEquipo = e.idEquipo
                    ORDER BY j.nombreJugador ASC
                """;

                try (Connection con = Conexion.getConnection();
                     ResultSet rs = con.createStatement().executeQuery(sql)) {

                    while (rs.next()) {
                        temp.add(new JugadorItem(
                                rs.getString("idJugador"),
                                rs.getString("nombreJugador"),
                                rs.getString("nombreCiudad"),
                                rs.getString("fechaNacimiento"),
                                rs.getString("numeroJugador"),
                                rs.getString("nombreEquipo")
                        ));
                    }
                }

                return temp;
            }
        };

        task.setOnSucceeded(e -> {
            lista.setAll(task.getValue());
            tablaJugadores.setItems(lista);
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

            private final HBox contenedor = new HBox(8);

            {
                contenedor.setAlignment(Pos.CENTER);

                btnUpdate.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");

                btnUpdate.setOnAction(e -> editarJugador(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> eliminarJugador(getTableView().getItems().get(getIndex()).getId()));

                contenedor.getChildren().addAll(btnUpdate, btnDelete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }


    private void editarJugador(JugadorItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Jugador/JugadorEditarVisual.fxml"));
            Parent modal = loader.load();

            JugadorEditar controller = loader.getController();
            controller.cargarJugador(item);
            controller.setJugadorListarController(this);

            BorderPane root = (BorderPane) tablaJugadores.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            modal.setOpacity(0);
            content.getChildren().add(modal);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            txtBuscar.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void eliminarJugador(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Jugador");
        alert.setContentText("¿Desea eliminar el jugador con ID " + id + "?");

        if (alert.showAndWait().get() != ButtonType.OK) return;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Jugador WHERE idJugador = ?")) {

            ps.setString(1, id);
            ps.executeUpdate();

            cargarJugadoresAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Jugador/JugadorRegistrarVisual.fxml"));
            Parent modal = loader.load();

            JugadorRegistrar controller = loader.getController();
            controller.setJugadorListarController(this);

            BorderPane root = (BorderPane) tablaJugadores.getScene().getRoot();
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
            com.example.base_de_datos.PaginaPrincipal.volverAlDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
