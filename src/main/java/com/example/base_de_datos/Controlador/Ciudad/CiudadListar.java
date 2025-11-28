package com.example.base_de_datos.Controlador.Ciudad;

import com.example.base_de_datos.Conexion.Conexion;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CiudadListar {

    @FXML private TextField txtBuscar;
    @FXML private TableView<CiudadItem> tablaCiudades;
    @FXML private TableColumn<CiudadItem, String> colId;
    @FXML private TableColumn<CiudadItem, String> colNombre;
    @FXML private TableColumn<CiudadItem, Void> colAcciones;

    private ObservableList<CiudadItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tablaCiudades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        centrarColumnas();
        cargarCiudades();
        agregarBotones();


        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));
    }


    private void filtrarTabla(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tablaCiudades.setItems(lista);
            return;
        }

        String lower = filtro.toLowerCase();

        ObservableList<CiudadItem> filtrada = FXCollections.observableArrayList();

        for (CiudadItem c : lista) {
            if (c.getNombre().toLowerCase().contains(lower)) {
                filtrada.add(c);
            }
        }

        tablaCiudades.setItems(filtrada);
    }


    void cargarCiudades() {
        lista.clear();
        String query = "SELECT idCiudad, nombreCiudad FROM Ciudad";

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                lista.add(new CiudadItem(
                        rs.getString("idCiudad"),
                        rs.getString("nombreCiudad")
                ));
            }

            tablaCiudades.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
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


                btnDelete.setOnAction(e -> {
                    CiudadItem item = getTableView().getItems().get(getIndex());
                    eliminarCiudad(item.getId());
                });


                btnUpdate.setOnAction(e -> {
                    CiudadItem item = getTableView().getItems().get(getIndex());
                    abrirVentanaActualizar(item);
                });

                contenedor.getChildren().addAll(btnUpdate, btnDelete);
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }


    private void eliminarCiudad(String id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Ciudad");
        alert.setContentText("¿Desea eliminar la ciudad con ID " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String query = "DELETE FROM Ciudad WHERE idCiudad = ?";

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, id);
                ps.executeUpdate();

                cargarCiudades();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void abrirVentanaActualizar(CiudadItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Ciudad/CiudadEditarVisual.fxml"));
            Parent modal = loader.load();

            CiudadEditar controller = loader.getController();
            controller.cargarCiudad(item);
            controller.setCiudadListarController(this);

            BorderPane root = (BorderPane) tablaCiudades.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            content.getChildren().removeIf(n -> "modalEditarCiudad".equals(n.getId()));

            modal.setId("modalEditarCiudad");
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
        colId.setStyle("-fx-alignment: CENTER;");
        colNombre.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
    }


    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Ciudad/CiudadRegistrarVisual.fxml"));
            Parent modal = loader.load();

            CiudadRegistrar controller = loader.getController();
            controller.setCiudadListarController(this);

            BorderPane root = (BorderPane) tablaCiudades.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            content.getChildren().removeIf(node -> "modalRegistrarCiudad".equals(node.getId()));

            modal.setId("modalRegistrarCiudad");
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
    private void volverAlMenuPrincipal() {
        try {
            PaginaPrincipal.volverAlDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
