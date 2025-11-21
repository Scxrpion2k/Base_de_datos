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

    private StackPane content; // Zona donde se cargan las vistas
    private ContextMenu activeMenu = null;
    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        // Crear menú superior
        HBox topMenu = createTopMenu(stage);
        root.setTop(topMenu);

        // Contenedor central
        content = new StackPane();
        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Sistema de Torneo de Baloncesto");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // ============================================================
    //  MENÚ SUPERIOR
    // ============================================================

    private HBox createTopMenu(Stage stage) {

        HBox menu = new HBox(35);
        menu.setPadding(new Insets(15, 20, 15, 20));
        menu.setStyle("-fx-background-color: black;");

        Label title = new Label("🏀 TORNEO BASKET");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        // Botones principales
        Button btnCiudad = createMenuButton("Ciudades");
        Button btnEquipo = createMenuButton("Equipos");
        Button btnJugador = createMenuButton("Jugadores");
        Button btnJuego = createMenuButton("Juegos");
        Button btnEstadistica = createMenuButton("Estadísticas");

        // Submenús
        addHoverMenu(btnCiudad, "Registrar Ciudad", "Listar Ciudades");
        addHoverMenu(btnEquipo, "Registrar Equipo", "Listar Equipos");
        addHoverMenu(btnJugador, "Registrar Jugador", "Listar Jugadores");
        addHoverMenu(btnJuego, "Registrar Juego", "Listar Juegos");
        addHoverMenu(btnEstadistica, "Registrar Estadística", "Listar Estadísticas");

        // Espaciador
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón usuario
        Button btnConfig = createMenuButton("Usuario");

        // Botón salir
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

    // ============================================================
    //  BOTONES DEL MENÚ PRINCIPAL (ESTILO NBA)
    // ============================================================

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

    // ============================================================
    //  SUBMENÚS (HOVER)
    // ============================================================

    private void addHoverMenu(Button button, String... options) {

        ContextMenu menu = new ContextMenu();

        for (String option : options) {
            MenuItem item = new MenuItem(option);
            item.setOnAction(e -> handleMenuSelection(option));
            menu.getItems().add(item);
        }

        // Mostrar menú al pasar mouse
        button.setOnMouseEntered(e -> {

            // Si otro menú está abierto → cerrarlo antes
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

        // Cerrar solo si sales del botón Y del menú
        button.setOnMouseExited(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(160));
            delay.setOnFinished(ev -> {

                // si saliste del botón y NO entraste al menú
                if (!button.isHover() && !menu.isShowing()) {
                    menu.hide();
                }
            });
            delay.play();
        });

        // Cuando el menú se oculta → limpiar estado
        menu.setOnHidden(e -> {
            if (activeMenu == menu) {
                activeMenu = null;
            }
        });

    }



    // ============================================================
    //  CARGAR VISTAS DINÁMICAS
    // ============================================================

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Pane view = loader.load();

                content.getChildren().clear();
                content.getChildren().add(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  MAIN
    // ============================================================

    public static void main(String[] args) {
        launch();
    }
}
