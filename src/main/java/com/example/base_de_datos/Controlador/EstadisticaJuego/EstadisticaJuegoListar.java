package com.example.base_de_datos.Controlador.EstadisticaJuego;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class EstadisticaJuegoListar extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Visual/EstadisticaJuegoListarVisual.fxml"));


            Scene scene = new Scene(root);
            primaryStage.setTitle("Ciudad");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
