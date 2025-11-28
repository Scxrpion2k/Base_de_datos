package com.example.base_de_datos.Controlador.Equipo;

import com.example.base_de_datos.Conexion.Conexion;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoListar {

    @FXML private TableView<EquipoItem> tablaEquipos;
    @FXML private TableColumn<EquipoItem, String> colId;
    @FXML private TableColumn<EquipoItem, String> colNombre;
    @FXML private TableColumn<EquipoItem, String> colCiudad;
    @FXML private TableColumn<EquipoItem, Void> colAcciones;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCerrar;
    @FXML private TextField txtBuscar;
    private EquipoListar equipoListarController;





    private final ObservableList<EquipoItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {


        tablaEquipos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        btnRegistrar.setOnAction(e -> abrirFormularioRegistro());

        centrarColumnas();
        agregarBotones();

        btnCerrar.setOnAction(e -> volverAlMenuPrincipal());

        cargarEquiposAsync();
        //activarFiltro();
    }

    public void cargarEquiposAsync() {

        Task<ObservableList<EquipoItem>> task = new Task<>() {
            @Override
            protected ObservableList<EquipoItem> call() throws Exception {

                ObservableList<EquipoItem> tempList = FXCollections.observableArrayList();

                String query = """
                        SELECT e.idequipo, e.nombre_equipo, c.nombre_ciudad
                        FROM Equipo e
                        INNER JOIN Ciudad c ON e.idciudad = c.idciudad
                        """;

                try (Connection con = Conexion.getConnection();
                     ResultSet rs = con.createStatement().executeQuery(query)) {

                    while (rs.next()) {
                        tempList.add(new EquipoItem(
                                rs.getString("idequipo"),
                                rs.getString("nombre_equipo"),
                                rs.getString("nombre_ciudad")
                        ));
                    }
                }
                return tempList;
            }
        };

        task.setOnSucceeded(e -> {
            lista.setAll(task.getValue());
            tablaEquipos.setItems(lista);
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    public void setEquipoListarController(EquipoListar controller) {
        this.equipoListarController = controller;
    }

    private void agregarBotones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnUpdate = new Button("Actualizar");
            private final Button btnDelete = new Button("Eliminar");
            private final HBox contenedor = new HBox(10);

            {
                contenedor.setAlignment(Pos.CENTER);

                btnUpdate.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");

                contenedor.getChildren().addAll(btnUpdate, btnDelete);

                btnUpdate.setStyle(
                        "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8;"
                );
                btnDelete.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;"
                );

                btnUpdate.setStyle(
                        "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                );
                btnDelete.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
                );

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


                btnDelete.setOnAction(e -> {
                    EquipoItem item = getTableView().getItems().get(getIndex());
                    eliminarEquipo(item.getId());
                });

                btnUpdate.setOnAction(e -> {
                    EquipoItem item = getTableView().getItems().get(getIndex());
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

    private void eliminarEquipo(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Equipo");
        alert.setContentText("¿Desea eliminar el equipo " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            String query = "DELETE FROM Equipo WHERE idequipo = ?";

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, id);
                ps.executeUpdate();

                cargarEquiposAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void centrarColumnas() {
        colId.setStyle("-fx-alignment: CENTER;");
        colNombre.setStyle("-fx-alignment: CENTER;");
        colCiudad.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
    }

//    private void activarFiltro() {
//        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {
//            String filtro = newValue.toLowerCase().trim();
//
//            if (filtro.isEmpty()) {
//                tablaEquipos.setItems(lista);
//                return;
//            }
//
//            ObservableList<EquipoItem> filtrada = FXCollections.observableArrayList();
//
//            for (EquipoItem item : lista) {
//                if (item.getId().toLowerCase().contains(filtro)
//                    || item.getNombre().toLowerCase().contains(filtro)
//                    || item.getCiudad().toLowerCase().contains(filtro)) {
//                    filtrada.add(item);
//                }
//            }
//
//            tablaEquipos.setItems(filtrada);
//        });
//    }


    private void abrirVentanaActualizar(EquipoItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Equipo/EquipoEditarVisual.fxml"));
            Parent modal = loader.load();
            EquipoEditar controller = loader.getController();
            controller.setEquipoListarController(this);

            controller.cargarEquipo(item);

            BorderPane root = (BorderPane) tablaEquipos.getScene().getRoot();
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
            PaginaPrincipal.volverAlDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Equipo/EquipoRegistrarVisual.fxml"));
            Parent modal = loader.load();

            EquipoRegistrar controller = loader.getController();
            controller.setEquipoListarController(this);

            BorderPane root = (BorderPane) tablaEquipos.getScene().getRoot();
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





}