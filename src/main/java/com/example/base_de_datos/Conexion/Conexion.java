package com.example.base_de_datos.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:sqlserver://wbrg0001.database.windows.net:1433;"
                    + "database=Torneo(WBRG0001);"
                    + "user=wbrg0001@wbrg0001;"
                    + "password=Wilmer1904;"
                    + "encrypt=true;"
                    + "trustServerCertificate=false;"
                    + "hostNameInCertificate=*.database.windows.net;"
                    + "loginTimeout=30;";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL);
            System.out.println("¡Conexión exitosa a Azure SQL!");
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
