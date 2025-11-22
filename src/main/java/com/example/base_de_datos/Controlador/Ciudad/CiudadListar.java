package com.example.base_de_datos.Controlador.Ciudad;

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

public class CiudadListar {

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

        cargarCiudades();
        agregarBotones();
    }

    void cargarCiudades() {

        lista.clear();

        String query = "SELECT idciudad, nombre_ciudad FROM Ciudad";

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                lista.add(new CiudadItem(
                        rs.getString("idciudad"),
                        rs.getString("nombre_ciudad")
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

            private final HBox contenedor = new HBox(10, btnUpdate, btnDelete);

            {
                btnUpdate.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");

                // Botón Eliminar
                btnDelete.setOnAction(e -> {
                    CiudadItem item = getTableView().getItems().get(getIndex());
                    eliminarCiudad(item.getId());
                });

                // Botón Actualizar
                btnUpdate.setOnAction(e -> {
                    CiudadItem item = getTableView().getItems().get(getIndex());
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

    private void eliminarCiudad(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Ciudad");
        alert.setContentText("¿Desea eliminar la ciudad " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            String query = "DELETE FROM Ciudad WHERE idciudad = ?";

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
            Parent root = loader.load();

            // Pasar datos a la ventana de edición
            CiudadEditar controller = loader.getController();
            controller.cargarCiudad(item);

            // PASAR REFERENCIA AL LISTADO PARA REFRESCAR
            controller.setCiudadListarController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Actualizar Ciudad");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void volverAlMenuPrincipal() {
        BorderPane root = (BorderPane) tablaCiudades.getScene().getRoot();
        StackPane content = (StackPane) root.getCenter();
        content.getChildren().clear();
    }



}
