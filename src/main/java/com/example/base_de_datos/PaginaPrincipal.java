package com.example.base_de_datos;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;



public class PaginaPrincipal extends Application {

    private StackPane content;
    private ContextMenu activeMenu = null;
    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();


        HBox topMenu = createTopMenu(stage);
        root.setTop(topMenu);

        content = new StackPane();
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


        addHoverMenu(btnCiudad, "Registrar Ciudad", "Listar Ciudades");
        addHoverMenu(btnEquipo, "Registrar Equipo", "Listar Equipos");
        addHoverMenu(btnJugador, "Registrar Jugador", "Listar Jugadores");
        addHoverMenu(btnJuego, "Registrar Juego", "Listar Juegos");
        addHoverMenu(btnEstadistica, "Registrar Estadística", "Listar Estadísticas");


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
                btnConfig,
                btnSalir
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

    private void addHoverMenu(Button button, String... options) {

        ContextMenu menu = new ContextMenu();

        for (String option : options) {
            MenuItem item = new MenuItem(option);
            item.setOnAction(e -> handleMenuSelection(option));
            menu.getItems().add(item);
        }

        button.setOnMouseEntered(e -> {

            if (activeMenu != null && activeMenu != menu) {
                activeMenu.hide();
            }

            activeMenu = menu;

            if (!menu.isShowing()) {
                menu.show(button,
                        button.localToScreen(0, button.getHeight()).getX(),
                        button.localToScreen(0, button.getHeight()).getY());
            }
        });


        button.setOnMouseExited(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(160));
            delay.setOnFinished(ev -> {

                if (!button.isHover() && !menu.isShowing()) {
                    menu.hide();
                }
            });
            delay.play();
        });

        menu.setOnHidden(e -> {
            if (activeMenu == menu) {
                activeMenu = null;
            }
        });

    }


    private void handleMenuSelection(String option) {

        System.out.println("Seleccionaste: " + option);

        try {

            String fxml = null;

            switch (option) {

                case "Registrar Ciudad":
                    fxml = "CiudadRegistrarVisual.fxml";
                    break;

                case "Listar Ciudades":
                    fxml = "CiudadVisualListar.fxml";
                    break;

                case "Registrar Equipo":
                    fxml = "EquipoRegistrarVisual.fxml";
                    break;

                case "Listar Equipos":
                    fxml = "EquipoVisualListar.fxml";
                    break;

                case "Registrar Jugador":
                    fxml = "JugadorRegistrarVisual.fxml";
                    break;

                case "Listar Jugadores":
                    fxml = "JugadorVisualListar.fxml";
                    break;

                case "Registrar Juego":
                    fxml = "EstadisticaJuegoRegistrarVisual.fxml";
                    break;

                case "Listar Juegos":
                    fxml = "EstadisticaJuegoVisualListar.fxml";
                    break;

                case "Registrar Estadística":
                    fxml = "EstadisticaRegistrarVisual.fxml";
                    break;

                case "Listar Estadísticas":
                    fxml = "EstadisticaVisualListar.fxml";
                    break;
            }

            if (fxml != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/" + fxml));
                Pane view = loader.load();

                content.getChildren().clear();
                content.getChildren().add(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
