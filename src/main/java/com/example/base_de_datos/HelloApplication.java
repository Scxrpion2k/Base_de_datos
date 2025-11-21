package com.example.base_de_datos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {


        BorderPane root = new BorderPane();


        HBox topMenu = createTopMenu(stage);
        root.setTop(topMenu);


        StackPane content = new StackPane();
        root.setCenter(content);


        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Sistema de Torneo de Baloncesto");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox createTopMenu(Stage stage) {

        HBox menu = new HBox(35);
        menu.setPadding(new Insets(15, 20, 15, 20));
        menu.setStyle("-fx-background-color: black;");

        Label title = new Label("🏀 TORNEO BASKET");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");


        Button btnCiudad = createMenuButton("Ciudades");
        Button btnEquipo = createMenuButton("Equipos");
        Button btnJugador = createMenuButton("Jugadores");
        Button btnJuego = createMenuButton("Juegos");
        Button btnEstadistica = createMenuButton("Estadísticas");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnConfig = createMenuButton("Usuario");


        Button btnSalir = new Button("Salir");
        btnSalir.setStyle("-fx-background-color: #F0B501; -fx-text-fill: black; -fx-font-weight: bold;");
        btnSalir.setOnAction(e -> stage.close());

        menu.getChildren().addAll(
                title,
                btnCiudad, btnEquipo, btnJugador, btnJuego, btnEstadistica,
                spacer,
                btnConfig, btnSalir
        );

        return menu;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);

        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #F0B501;" +
                        "-fx-font-size: 14px;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"
        ));

        return btn;
    }

    public static void main(String[] args) {
        launch();
    }
}
