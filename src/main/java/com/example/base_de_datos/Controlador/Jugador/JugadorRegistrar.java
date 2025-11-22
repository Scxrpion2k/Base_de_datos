package com.example.base_de_datos.Controlador.Jugador;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JugadorRegistrar extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Visual/JugadorRegistrarVisual.fxml"));


            Scene scene = new Scene(root);
            primaryStage.setTitle("Ciudad");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void volverAlMenuPrincipal(ActionEvent actionEvent) {
    }

    public void guardarJugador(ActionEvent actionEvent) {
    }

    public void limpiar(ActionEvent actionEvent) {
    }
}
