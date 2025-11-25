package com.example.base_de_datos.Controlador.EstadisticaJuego;

import com.example.base_de_datos.Conexion.Conexion;
import com.example.base_de_datos.Controlador.Equipo.EquipoItem;
import com.example.base_de_datos.Controlador.Estadistica.EstadisticaItem;
import com.example.base_de_datos.Controlador.Juego.JuegoItem;
import com.example.base_de_datos.Controlador.Jugador.JugadorItem;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EstadisticaJuegoRegistrar {

    @FXML private TextField txtIdJuego;
    @FXML private ComboBox<EstadisticaItem> cmbEstadistica;
    @FXML private ComboBox<EquipoItem> cmbEquipo;
    @FXML private ComboBox<JugadorItem> cmbJugador;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<JuegoItem> cmbJuegos;
    @FXML private Label lblJuegos;

    private boolean seleccionarJuego = false;


    @FXML
    private AnchorPane rootRegistrar;

    private String idJuego;

    @FXML
    public void initialize() {
        cmbJuegos.setVisible(false);


        if (seleccionarJuego) {
            cmbJuegos.setVisible(true);
            cargarJuegos();
        }
    }




    public void setModoSeleccionJuego(boolean seleccionarJuego) {

        Platform.runLater(() -> {
            if (cmbJuegos != null) {
                cmbJuegos.setVisible(seleccionarJuego);
                if (seleccionarJuego) cargarJuegos();
            }
        });
    }






    public void setIdJuego(String idJuego) {
        this.idJuego = idJuego;

        Platform.runLater(() -> {
            txtIdJuego.setText(idJuego);
            cargarEstadisticas();
            cargarEquipos();
        });
    }


    private void cargarEstadisticas() {
        String sql = "SELECT idEstadistica, descripcionEstadistica FROM Estadistica";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ObservableList<EstadisticaItem> lista = FXCollections.observableArrayList();
            while (rs.next()) {
                lista.add(new EstadisticaItem(rs.getString("idEstadistica"), rs.getString("descripcionEstadistica")));
            }
            cmbEstadistica.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cargarEquipos() {
        String sql = "SELECT idEquipo, nombreEquipo, idCiudad " +
                "FROM Equipo " +
                "WHERE idEquipo IN (SELECT idEquipoA FROM Juego WHERE idJuego = ?) " +
                "   OR idEquipo IN (SELECT idEquipoB FROM Juego WHERE idJuego = ?)";


        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idJuego);
            ps.setString(2, idJuego);

            ResultSet rs = ps.executeQuery();

            ObservableList<EquipoItem> equipos = FXCollections.observableArrayList();
            while (rs.next()) {
                String id = rs.getString("idEquipo");
                String nombre = rs.getString("nombreEquipo");
                String ciudad = rs.getString("idCiudad");
                equipos.add(new EquipoItem(id, nombre, ciudad));
            }

            cmbEquipo.setItems(equipos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void cargarJugadores() {
        // Obtener el equipo seleccionado
        EquipoItem equipoSeleccionado = cmbEquipo.getValue();
        if (equipoSeleccionado == null) return;

        String idEquipo = equipoSeleccionado.getId(); // <- usar el ID

        // Limpiar jugadores anteriores
        cmbJugador.getItems().clear();

        String sql = "SELECT idJugador, nombreJugador, idCiudadNacimiento, fechaNacimiento, numeroJugador, idEquipo " +
                "FROM Jugador " +
                "WHERE idEquipo = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idEquipo); // <- pasar ID, no nombre

            try (ResultSet rs = ps.executeQuery()) {
                ObservableList<JugadorItem> jugadores = FXCollections.observableArrayList();

                while (rs.next()) {
                    String id = rs.getString("idJugador");
                    String nombre = rs.getString("nombreJugador");
                    String ciudad = rs.getString("idCiudadNacimiento");
                    String fecha = rs.getString("fechaNacimiento");
                    String numero = rs.getString("numeroJugador");
                    String equipo = rs.getString("idEquipo");

                    jugadores.add(new JugadorItem(id, nombre, ciudad, fecha, numero, equipo));
                }

                cmbJugador.setItems(jugadores); // ComboBox con objetos JugadorItem
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarJuegos() {
        // Limpiar el ComboBox antes de cargar
        cmbJuegos.getItems().clear();

        String sql = """
            SELECT 
                j.idJuego,
                j.descripcionJuego,
                A.nombreEquipo AS equipoA,
                B.nombreEquipo AS equipoB,
                j.fechaJuego
            FROM Juego j
            INNER JOIN Equipo A ON j.idEquipoA = A.idEquipo
            INNER JOIN Equipo B ON j.idEquipoB = B.idEquipo
            ORDER BY j.fechaJuego DESC
            """;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ObservableList<JuegoItem> juegos = FXCollections.observableArrayList();

            while (rs.next()) {
                String id = rs.getString("idJuego");
                String descripcion = rs.getString("descripcionJuego");
                String equipoA = rs.getString("equipoA");
                String equipoB = rs.getString("equipoB");
                String fecha = rs.getString("fechaJuego").split(" ")[0]; // solo fecha

                // JuegoItem debe tener un constructor: id, descripcion, equipoA, equipoB, fecha
                juegos.add(new JuegoItem(id, descripcion, equipoA, equipoB, fecha));
            }

            cmbJuegos.setItems(juegos);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar los juegos: " + e.getMessage()).showAndWait();
        }
    }






    @FXML
    public void guardarEstadistica(ActionEvent actionEvent) {

        String idJuegoFinal;
        if (seleccionarJuego) {
            JuegoItem juegoSeleccionado = cmbJuegos.getValue();
            if (juegoSeleccionado == null) {
                new Alert(Alert.AlertType.WARNING, "Debe seleccionar un juego.").showAndWait();
                return;
            }
            idJuegoFinal = juegoSeleccionado.getIdJuego();
        } else {
            idJuegoFinal = this.idJuego; // idJuego ya asignado desde setIdJuego
        }

        // Obtener los objetos seleccionados
        EstadisticaItem estadisticaSeleccionada = cmbEstadistica.getValue();
        EquipoItem equipoSeleccionado = cmbEquipo.getValue();
        JugadorItem jugadorSeleccionado = cmbJugador.getValue();
        String cantidadStr = txtCantidad.getText();

        // Validar que todos los campos estén completos
        if (estadisticaSeleccionada == null || equipoSeleccionado == null || jugadorSeleccionado == null || cantidadStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe completar todos los campos.").showAndWait();
            return;
        }

        // Convertir cantidad a entero
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Cantidad inválida.").showAndWait();
            return;
        }

        // Obtener los IDs
        String idEstadistica = estadisticaSeleccionada.getId();
        String idEquipo = equipoSeleccionado.getId();
        String idJugador = jugadorSeleccionado.getId();

        try (Connection conn = Conexion.getConnection();
             PreparedStatement psInsert = conn.prepareStatement(
                     "INSERT INTO dbo.EstadisticaDeJuego(idJuego, idEstadisticaRegistrar, idEquipo, idJugador, cantidad) VALUES (?, ?, ?, ?, ?)")) {

            // Asignar parámetros
            psInsert.setString(1, idJuego);
            psInsert.setString(2, idEstadistica);
            psInsert.setString(3, idEquipo);
            psInsert.setString(4, idJugador);
            psInsert.setInt(5, cantidad);

            // Ejecutar inserción
            psInsert.executeUpdate();

            // Confirmación
            new Alert(Alert.AlertType.INFORMATION, "Estadística registrada correctamente.").showAndWait();
            limpiar();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al registrar la estadística: " + e.getMessage()).showAndWait();
        }
    }





    @FXML
    public void limpiar() {
        cmbEstadistica.getSelectionModel().clearSelection();
        cmbEquipo.getSelectionModel().clearSelection();
        cmbJugador.getItems().clear();
        txtCantidad.clear();
    }

    @FXML
    public void cerrarVentana(ActionEvent actionEvent) {
        try {
            if (rootRegistrar.getParent() instanceof StackPane parent) {
                FadeTransition fade = new FadeTransition(Duration.millis(200), rootRegistrar);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(e -> parent.getChildren().remove(rootRegistrar));
                fade.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}







