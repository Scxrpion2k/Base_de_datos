package Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertarCiudad {

    // Método para insertar una ciudad
    public static void insertarCiudad(String idCiudad, String nombre) {
        String sql = "INSERT INTO Ciudad (idciudad, nombreCiudad) VALUES (?, ?)";

        try (Connection con = ConexionBaseDeDatos.getConnection() ;
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idCiudad); // primer ? → idciudad
            ps.setString(2, nombre);   // segundo ? → nombre

            int filas = ps.executeUpdate(); // Ejecuta la inserción
            System.out.println("Filas insertadas: " + filas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Ejemplo: insertar una ciudad
        insertarCiudad("C005", "Lima");
        insertarCiudad("C006", "Cusco");
    }
}
