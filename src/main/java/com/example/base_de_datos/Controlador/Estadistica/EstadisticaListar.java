package com.example.base_de_datos.Controlador.Estadistica;

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
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EstadisticaListar {

    @FXML private TableView<EstadisticaItem> tablaEstadisticas;
    @FXML private TableColumn<EstadisticaItem, String> colId;
    @FXML private TableColumn<EstadisticaItem, String> colDescripcion;
    @FXML private TableColumn<EstadisticaItem, Integer> colValor;
    @FXML private TableColumn<EstadisticaItem, Void> colAcciones;
    @FXML private TextField txtBuscar;

    private ObservableList<EstadisticaItem> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        tablaEstadisticas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("idEstadistica"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionEstadistica"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        centrarColumnas();
        cargarEstadisticas();
        agregarBotones();
       // activarFiltro();
    }

    void cargarEstadisticas() {

        lista.clear();

        String query = "SELECT idEstadistica, descripcionEstadistica, valor FROM Estadistica";

        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {

            while (rs.next()) {
                lista.add(new EstadisticaItem(
                        rs.getString("idEstadistica"),
                        rs.getString("descripcionEstadistica"),
                        rs.getInt("valor")
                ));
            }

            tablaEstadisticas.setItems(lista);

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
                    EstadisticaItem item = getTableView().getItems().get(getIndex());
                    eliminarEstadistica(item.getIdEstadistica());
                });

                btnUpdate.setOnAction(e -> {
                    EstadisticaItem item = getTableView().getItems().get(getIndex());
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

    private void eliminarEstadistica(String id) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Estadística");
        alert.setContentText("¿Desea eliminar la estadística " + id + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            String query = "DELETE FROM Estadistica WHERE idEstadistica = ?";

            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, id);
                ps.executeUpdate();

                cargarEstadisticas();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void activarFiltro() {
//        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {
//            String filtro = newValue.toLowerCase().trim();
//
//            if (filtro.isEmpty()) {
//                tablaEstadisticas.setItems(lista);
//                return;
//            }
//
//            ObservableList<EstadisticaItem> filtrada = FXCollections.observableArrayList();
//
//            for (EstadisticaItem item : lista) {
//                if (item.getIdEstadistica().toLowerCase().contains(filtro)
//                        || item.getDescripcionEstadistica().toLowerCase().contains(filtro)
//                        || String.valueOf(item.getValor()).contains(filtro)) {
//                    filtrada.add(item);
//                }
//            }
//
//            tablaEstadisticas.setItems(filtrada);
//        });
//    }


    private void abrirVentanaActualizar(EstadisticaItem item) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Estadistica/EstadisticaEditarVisual.fxml"));
            Parent modal = loader.load();

            EstadisticaEditar controller = loader.getController();
            controller.cargarDatos(item);
            controller.setEstadisticaListarController(this);

            BorderPane root = (BorderPane) tablaEstadisticas.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            content.getChildren().removeIf(n -> "modalEditarEstadistica".equals(n.getId()));

            modal.setId("modalEditarEstadistica");
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

    public void abrirFormularioRegistro() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/Estadistica/EstadisticaRegistrarVisual.fxml"));
            Parent modal = loader.load();

            EstadisticaRegistrar controller = loader.getController();
            controller.setEstadisticaListarController(this);

            BorderPane root = (BorderPane) tablaEstadisticas.getScene().getRoot();
            StackPane content = (StackPane) root.getCenter();

            content.getChildren().removeIf(n -> "modalRegistrarEstadistica".equals(n.getId()));

            modal.setId("modalRegistrarEstadistica");
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
        colDescripcion.setStyle("-fx-alignment: CENTER;");
        colValor.setStyle("-fx-alignment: CENTER;");
        colAcciones.setStyle("-fx-alignment: CENTER;");
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
