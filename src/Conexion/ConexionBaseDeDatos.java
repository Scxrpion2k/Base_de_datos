package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDeDatos {
    private static final String URL = "jdbc:sqlserver://Localhost:1433;databaseName=Torneo(WBRG0001);encrypt=false;integratedSecurity=true";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL);
            System.out.println("¡Conexión exitosa a SQL Server!");
            return con;
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) {
        getConnection();
    }
}
