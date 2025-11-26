package com.example.base_de_datos.Controlador.Inicio;

import com.example.base_de_datos.Controlador.EstadisticaJuego.EstadisticaJuegoReporte;
import com.example.base_de_datos.Conexion.Conexion;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import javafx.fxml.FXMLLoader;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

public class InicioDashboard {

    @FXML private ScrollPane scrollJuegos;
    @FXML private HBox contenedorJuegos;

    @FXML private TextField txtBuscarJuego;
    @FXML private Button btnBuscarJuego;

    private final HashMap<String, String> logos = new HashMap<>();

    @FXML
    public void initialize() {

        // Mapear logos locales
        logos.put("los angeles lakers", "/Logo/Los_Angeles_Lakers_logo.svg.png");
        logos.put("golden state warriors", "/Logo/Golden_State_Warriors_logo.svg.png");
        logos.put("chicago bulls", "/Logo/Chicago_Bulls_logo.svg.png");
        logos.put("miami heat", "/Logo/Miami_Heat_logo.svg.png");
        logos.put("boston celtics", "/Logo/Boston_Celtics.svg.png");


        double anchoTarjeta = 380;
        double espacio = 45;
        double anchoTotal = (anchoTarjeta * 3) + (espacio * 2);

        contenedorJuegos.setPrefWidth(anchoTotal);
        contenedorJuegos.setMaxWidth(anchoTotal);
        contenedorJuegos.setAlignment(Pos.CENTER);

        scrollJuegos.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            contenedorJuegos.setPrefWidth(Math.max(anchoTotal, newVal.getWidth()));
        });


        scrollJuegos.setFitToHeight(true);
        scrollJuegos.setFitToWidth(true);
        scrollJuegos.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollJuegos.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollJuegos.setPannable(true);

        scrollJuegos.setPadding(new Insets(10, 0, 0, 0));

        btnBuscarJuego.setOnAction(e -> buscarJuego());

        cargarTodosLosJuegos();
    }

    private Image obtenerLogo(String equipo) {

        equipo = equipo.trim().toLowerCase();

        String ruta = logos.getOrDefault(equipo, "/Logo/Chicago_Bulls_logo.svg.png");

        var stream = getClass().getResourceAsStream(ruta);

        if (stream == null) {
            System.out.println("⚠ ERROR: No se encontró el archivo: " + ruta);
            return new Image(getClass().getResourceAsStream("/Logo/Chicago_Bulls_logo.svg.png"));
        }

        return new Image(stream);
    }

    private void cargarTodosLosJuegos() {

        contenedorJuegos.getChildren().clear();

        String sql = """
    SELECT j.idJuego,
           j.descripcionJuego,
           RTRIM(A.nombreEquipo) AS equipoA,
           RTRIM(B.nombreEquipo) AS equipoB,
           CONVERT(VARCHAR(10), j.fechaJuego, 105) AS fecha
    FROM Juego j
    INNER JOIN Equipo A ON j.idEquipoA = A.idEquipo
    INNER JOIN Equipo B ON j.idEquipoB = B.idEquipo
    ORDER BY j.fechaJuego DESC
""";



        try (Connection con = Conexion.getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {

            while (rs.next()) {

                String id = rs.getString("idJuego");
                String desc = rs.getString("descripcionJuego").trim();
                String equipoA = rs.getString("equipoA").trim();
                String equipoB = rs.getString("equipoB").trim();
                String fecha = rs.getString("fecha");

                VBox card = crearTarjetaJuego(id, desc, equipoA, equipoB, fecha);
                card.setOpacity(0);
                contenedorJuegos.getChildren().add(card);

                FadeTransition fade = new FadeTransition(Duration.millis(250), card);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox crearTarjetaJuego(String id, String desc, String equipoA, String equipoB, String fecha) {

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));


        card.setPrefSize(380, 230);
        card.setMinSize(380, 230);
        card.setMaxSize(380, 230);

        String estiloNormal = """
        -fx-background-color: #ffffff;
        -fx-background-radius: 22;
        -fx-border-color: #E4E4E4;
        -fx-border-radius: 22;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 15, 0, 0, 5);
    """;

        String estiloHover = """
        -fx-background-color: #ffffff;
        -fx-background-radius: 22;
        -fx-border-color: #0d6efd;
        -fx-border-radius: 22;
        -fx-effect: dropshadow(gaussian, rgba(13,110,253,0.35), 22, 0, 0, 8);
    """;

        card.setStyle(estiloNormal);


        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e -> card.setStyle(estiloNormal));

        // ✅ LOGOS
        ImageView imgA = new ImageView(obtenerLogo(equipoA));
        imgA.setFitWidth(65);
        imgA.setFitHeight(65);

        ImageView imgB = new ImageView(obtenerLogo(equipoB));
        imgB.setFitWidth(65);
        imgB.setFitHeight(65);

        Label estado = new Label("FINALIZADO");
        estado.setStyle("""
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-text-fill: #666;
    -fx-background-color: #F0F0F0;
    -fx-padding: 4 10;
    -fx-background-radius: 10;
    -fx-border-radius: 10;
""");

        HBox logoRow = new HBox(18, imgA, estado, imgB);
        logoRow.setAlignment(Pos.CENTER);


        Label lblEquipos = new Label(equipoA + " vs " + equipoB);
        lblEquipos.setFont(Font.font("Arial", 18));
        lblEquipos.setStyle("-fx-font-weight: bold; -fx-text-fill:#111;");

        Label lblFecha = new Label(fecha);
        lblFecha.setStyle("-fx-font-size:13px; -fx-text-fill:#666;");

        Button ver = new Button("Ver detalles");
        ver.setStyle("""
        -fx-background-color:#0d6efd;
        -fx-text-fill:white;
        -fx-font-size:14px;
        -fx-font-weight:bold;
        -fx-background-radius:14;
        -fx-padding:8 22;
        
    """);
        ver.setOnMouseEntered(e -> ver.setStyle("""
    -fx-background-color:#0b5ed7;
    -fx-text-fill:white;
    -fx-font-size:14px;
    -fx-font-weight:bold;
    -fx-background-radius:14;
    -fx-padding:8 22;
"""));

        ver.setOnMouseExited(e -> ver.setStyle("""
    -fx-background-color:#0d6efd;
    -fx-text-fill:white;
    -fx-font-size:14px;
    -fx-font-weight:bold;
    -fx-background-radius:14;
    -fx-padding:8 22;
"""));

        ver.setOnAction(e -> abrirReporteTabJueg(id));

        ScaleTransition grow = new ScaleTransition(Duration.millis(150), card);
        grow.setToX(1.04);
        grow.setToY(1.04);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), card);
        shrink.setToX(1);
        shrink.setToY(1);

        card.setOnMouseEntered(e -> {
            card.setStyle(estiloHover);
            grow.playFromStart();
        });

        card.setOnMouseExited(e -> {
            card.setStyle(estiloNormal);
            shrink.playFromStart();
        });

        card.getChildren().addAll(lblEquipos, logoRow, lblFecha, ver);

        return card;
    }




    private void buscarJuego() {

        String id = txtBuscarJuego.getText().trim();

        if (id.isEmpty()) {
            alerta("Debe escribir un número de juego.");
            return;
        }

        String sql = "SELECT idJuego FROM Juego WHERE idJuego = ?";

        try (Connection con = Conexion.getConnection();
             var ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                abrirReporteTabJueg(id);
                txtBuscarJuego.clear();
            } else {
                alerta("No existe un juego con ID: " + id);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            alerta("Error al buscar el juego.");
        }
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    private void abrirReporteTabJueg(String idJuego) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Visual/EstadisticaJuego/EstadisticaJuegoReporteVisual.fxml"));

            Parent modal = loader.load();

            EstadisticaJuegoReporte controller = loader.getController();
            controller.cargarReporte(idJuego);

            StackPane root = (StackPane) scrollJuegos.getScene().lookup("#mainContent");

            modal.setOpacity(0);
            root.getChildren().add(modal);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modal);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
