package com.example.base_de_datos.Controlador.Jugador;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class JugadorListar extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Visual//JugadorVisualListar.fxml")));


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
}
