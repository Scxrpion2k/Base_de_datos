module com.example.base_de_datos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.base_de_datos to javafx.fxml;
    exports com.example.base_de_datos;
}

