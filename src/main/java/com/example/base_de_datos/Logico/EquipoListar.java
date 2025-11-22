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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoListar {

    @FXML private TableView<EquipoItem> tablaEquipos;
    @FXML private TableColumn<EquipoItem, String> colId;
    @FXML private TableColumn<EquipoItem, String> colNombre;
    @FXML private TableColumn<EquipoItem, String> colCiudad;
    @FXML private TableColumn<EquipoItem, Void> colAcciones;

    private ObservableList<EquipoItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tablaEquipos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

        cargarEquipos();
        agregarBotones();
    }

    private void cargarEquipos() {

        lista.clear();

        String query = """
            SELECT e.idEquipo, e.nombreEquipo, c.nombreCiudad
            FROM Equipo e
            INNER JOIN Ciudad c ON e.idCiudad = c.idCiudad
        """;

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                lista.add(new EquipoItem(
                        rs.getString("idEquipo"),
                        rs.getString("nombreEquipo"),
                        rs.getString("nombreCiudad")
                ));
            }

            tablaEquipos.setItems(lista);

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
                    EquipoItem item = getTableView().getItems().get(getIndex());
                    eliminarEquipo(item.getId());
                });

                // Botón Actualizar
                btnUpdate.setOnAction(e -> {
                    EquipoItem item = getTableView().getItems().get(getIndex());
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

    private void eliminarEquipo(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Equipo");
        alert.setContentText("¿Desea eliminar el equipo " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            String query = "DELETE FROM Equipo WHERE idEquipo = ?";

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, id);
                ps.executeUpdate();

                cargarEquipos();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirVentanaActualizar(EquipoItem item) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/EquipoEditarVisual.fxml"));
            Parent root = loader.load();

            // Pasar datos a la ventana de edición
            EquipoEditar controller = loader.getController();
            controller.cargarEquipo(item);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Actualizar Equipo");
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
