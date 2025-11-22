module com.example.base_de_datos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens com.example.base_de_datos to javafx.fxml;
    exports com.example.base_de_datos;

    opens com.example.base_de_datos.Controlador to javafx.fxml;
    exports com.example.base_de_datos.Controlador;
    exports com.example.base_de_datos.Controlador.Equipo;
    opens com.example.base_de_datos.Controlador.Equipo to javafx.fxml;
    exports com.example.base_de_datos.Controlador.Ciudad;
    opens com.example.base_de_datos.Controlador.Ciudad to javafx.fxml;
    exports com.example.base_de_datos.Controlador.Juego;
    opens com.example.base_de_datos.Controlador.Juego to javafx.fxml;

}