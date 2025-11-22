package com.example.base_de_datos.Logico;

import com.example.base_de_datos.Conexion.Conexion;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EquipoEditar {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCiudad;

    private String idOriginal;

    public void cargarEquipo(EquipoItem item) {
        idOriginal = item.getId();
        txtId.setText(item.getId());
        txtNombre.setText(item.getNombre());
        cmbCiudad.setValue(item.getCiudad());
    }

    @FXML
    public void guardarCambios() {

        String query = """
            UPDATE Equipo
            SET idEquipo = ?, nombreEquipo = ?, idCiudad =
                (SELECT idCiudad FROM Ciudad WHERE nombreCiudad = ?)
            WHERE idEquipo = ?
        """;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, txtId.getText());
            ps.setString(2, txtNombre.getText());
            ps.setString(3, cmbCiudad.getValue());
            ps.setString(4, idOriginal);

            ps.executeUpdate();

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Actualizado con éxito.");
            ok.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
