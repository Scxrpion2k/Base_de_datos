package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.ConexionBaseDeDatos;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SPController extends Application {

    @FXML
    private TableView<ObservableList<String>> tableView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/SPView.fxml"));
        AnchorPane root = loader.load();

        SPController controller = loader.getController();
        controller.mostrarSP();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void mostrarSP() {
        try {
            Connection conn = ConexionBaseDeDatos.getConnection();
            if (conn == null) return;

            CallableStatement cs = conn.prepareCall("{call dbo.tabJueg}");
            ResultSet rs = cs.executeQuery();

            tableView.getColumns().clear();


            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                final int colIndex = i - 1;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
                tableView.getColumns().add(col);
            }


            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            tableView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
