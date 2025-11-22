module com.example.base_de_datos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens com.example.base_de_datos to javafx.fxml;
    exports com.example.base_de_datos;

    opens com.example.base_de_datos.Logico to javafx.fxml;
    exports com.example.base_de_datos.Logico;

}