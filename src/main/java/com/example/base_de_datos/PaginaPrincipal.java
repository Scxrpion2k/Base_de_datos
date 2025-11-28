package com.example.base_de_datos;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PaginaPrincipal extends Application {

    private static PaginaPrincipal instance;
    private StackPane content;
    private ContextMenu activeMenu = null;
    private Stage primaryStage;

    public PaginaPrincipal() {
        instance = this;
    }

    public static PaginaPrincipal getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();


        HBox topMenu = createTopMenu(stage);
        root.setTop(topMenu);

        content = new StackPane();
        content.setId("mainContent");
        content.setStyle("-fx-background-color: #F4F6F9;");
        root.setCenter(content);

        try {
            Parent inicio = FXMLLoader.load(getClass().getResource("/Visual/Inicio/InicioDashboardVisual.fxml"));
            content.getChildren().setAll(inicio);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 1400, 850);

        stage.setTitle("Sistema de Torneo de Baloncesto");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }



    private void showMainApplication() {
        BorderPane root = new BorderPane();

        HBox topMenu = createTopMenu(primaryStage);
        root.setTop(topMenu);

        content = new StackPane();
        content.setId("mainContent");
        content.setStyle("-fx-background-color: #F4F6F9;");
        root.setCenter(content);

        try {
            Parent inicio = FXMLLoader.load(getClass().getResource("/Visual/Inicio/InicioDashboardVisual.fxml"));
            content.getChildren().setAll(inicio);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene mainScene = new Scene(root, 1400, 850);

        primaryStage.setTitle("Sistema de Torneo de Baloncesto");
        primaryStage.setScene(mainScene);


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setX(0);
        primaryStage.setY(0);


        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private ImageView createIcon(String file, int size) {
        try {
            Image img = new Image(getClass().getResourceAsStream(file));
            ImageView view = new ImageView(img);
            view.setFitWidth(size);
            view.setFitHeight(size);
            return view;
        } catch (Exception e) {
            System.out.println("No se pudo cargar icono: " + file);
            ImageView fallback = new ImageView();
            fallback.setFitWidth(size);
            fallback.setFitHeight(size);
            fallback.setStyle("-fx-background-color: #F0B501; -fx-background-radius: 5;");
            return fallback;
        }
    }

    private ImageView iconoLocal(String file, int size) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/Logo/" + file));
            ImageView view = new ImageView(img);
            view.setFitWidth(size);
            view.setFitHeight(size);
            ColorAdjust adjust = new ColorAdjust();
            adjust.setBrightness(1.0);
            adjust.setContrast(1.0);
            adjust.setSaturation(-1.0);
            view.setEffect(adjust);
            return view;
        } catch (Exception e) {
            System.out.println("No se pudo cargar icono: /Logo/" + file);
            return new ImageView();
        }
    }

    private HBox createTopMenu(Stage stage) {
        HBox menu = new HBox(25);
        menu.setPadding(new Insets(12, 35, 12, 35));
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setStyle("""
            -fx-background-color: #0A0A0A;
            -fx-border-color: #1F1F1F;
            -fx-border-width: 0 0 2 0;
        """);
        menu.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.25)));

        Label title = new Label("🏀 TORNEO BASKET");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 19px;
            -fx-font-weight: bold;
        """);

        Button btnCiudad = createMenuButton("Ciudades", "city-buildings.png");
        Button btnEquipo = createMenuButton("Equipos", "groups.png");
        Button btnJugador = createMenuButton("Jugadores", "user.png");
        Button btnJuego = createMenuButton("Juegos", "timetable.png");
        Button btnEstadistica = createMenuButton("Estadísticas", "estadistica.png");




        addHoverMenu(btnCiudad, "Registrar Ciudad", "Lista Ciudades");
        addHoverMenu(btnEquipo, "Registrar Equipo", "Lista Equipos");
        addHoverMenu(btnJugador, "Registrar Jugador", "Lista Jugadores");
        addHoverMenu(btnJuego, "Registrar Juego", "Lista Juegos");
        addHoverMenu(btnEstadistica, "Registrar Estadistica", "Lista de Estadisticas", "Registrar Estadísticas De Juego" ,"Lista de Estadísticas De Juego");



        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        Button btnSalir = new Button("Salir");
        btnSalir.setStyle("""
            -fx-background-color: #F0B501;
            -fx-background-radius: 12;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-padding: 8 20;
        """);
        btnSalir.setOnAction(e -> stage.close());


        menu.getChildren().addAll(
                title,
                btnCiudad, btnEquipo, btnJugador, btnJuego, btnEstadistica,
                spacer,
                btnSalir
        );

        return menu;
    }

    private Button createMenuButton(String text, String iconFile) {
        ImageView icon = iconoLocal(iconFile, 18);
        Button btn = new Button(text, icon);
        btn.setGraphicTextGap(10);
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #E5E5E5;
            -fx-font-size: 15px;
            -fx-font-weight: 600;
        """);
        btn.setOnMouseEntered(e -> btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #F0B501;
            -fx-font-size: 15px;
            -fx-font-weight: 600;
        """));
        btn.setOnMouseExited(e -> btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #E5E5E5;
            -fx-font-size: 15px;
            -fx-font-weight: 600;
        """));
        return btn;
    }

    private void addHoverMenu(Button button, String... options) {
        ContextMenu menu = new ContextMenu();
        menu.setStyle("""
            -fx-background-color: #1B1B1B;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 5 0;
        """);

        for (String option : options) {
            MenuItem item = new MenuItem(option);
            item.setStyle("""
                -fx-text-fill: white;
                -fx-padding: 8 12;
                -fx-font-size: 14px;
            """);
            item.setOnAction(e -> handleMenuSelection(option));
            menu.getItems().add(item);
        }

        button.setOnMouseEntered(e -> {
            if (activeMenu != null && activeMenu != menu) activeMenu.hide();
            activeMenu = menu;
            if (!menu.isShowing()) {
                menu.show(button,
                        button.localToScreen(0, button.getHeight()).getX(),
                        button.localToScreen(0, button.getHeight()).getY() + 2);
            }
        });

        button.setOnMouseExited(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(120));
            delay.setOnFinished(ev -> {
                if (!button.isHover() && !menu.isShowing()) menu.hide();
            });
            delay.play();
        });

        menu.setOnHidden(e -> {
            if (activeMenu == menu) activeMenu = null;
        });
    }

    private void handleMenuSelection(String option) {
        try {
            String path;
            switch (option) {
                case "Registrar Ciudad" -> {
                    abrirModal("/Visual/Ciudad/CiudadRegistrarVisual.fxml", "modalCiudad");
                    return;
                }
                case "Registrar Equipo" -> {
                    abrirModal("/Visual/Equipo/EquipoRegistrarVisual.fxml", "modalRegistrarEquipo");
                    return;
                }
                case "Registrar Jugador" -> {
                    abrirModal("/Visual/Jugador/JugadorRegistrarVisual.fxml", "modalRegistrarJugador");
                    return;
                }
                case "Registrar Juego" -> {
                    abrirModal("/Visual/Juego/JuegoRegistrarVisual.fxml", "modalRegistrarJuego");
                    return;
                }

                case "Registrar Estadistica" -> {
                    abrirModal( "/Visual/Estadistica/EstadisticaRegistrarVisual.fxml", "modalEstadistica");
                    return;
                }

                case "Lista Ciudades" -> path = "/Visual/Ciudad/CiudadListarVisual.fxml";
                case "Lista Equipos" -> path = "/Visual/Equipo/EquipoListarVisual.fxml";
                case "Lista Jugadores" -> path = "/Visual/Jugador/JugadorListarVisual.fxml";
                case "Lista Juegos" -> path = "/Visual/Juego/JuegoListarVisual.fxml";
                case "Lista de Estadisticas" -> path = "/Visual/Estadistica/EstadisticaListarVisual.fxml";
                case "Registrar Estadísticas De Juego" -> path = "/Visual/EstadisticaJuego/EstadisticaJuegoListarVisual.fxml";
                case "Lista de Estadísticas De Juego" -> path = "/Visual/EstadisticaJuego/EstadisticaJuegoListarVisual.fxml";


                default -> { return; }
            }
            Pane view = PageManager.get(path);
            content.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirModal(String fxml, String modalId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent modal = loader.load();
            modal.setId(modalId);
            modal.setOpacity(0);
            content.getChildren().removeIf(node -> modalId.equals(node.getId()));
            content.getChildren().add(modal);
            FadeTransition fade = new FadeTransition(Duration.millis(180), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void volverAlDashboard() {
        try {
            Pane dash = PageManager.get("/Visual/Inicio/InicioDashboardVisual.fxml");
            instance.content.getChildren().setAll(dash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("""
            -fx-background-color: #1B1B1B;
            -fx-text-fill: white;
        """);
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}