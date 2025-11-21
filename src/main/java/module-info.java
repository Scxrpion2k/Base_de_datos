module com.example.base_de_datos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.base_de_datos to javafx.graphics, javafx.fxml;
    exports com.example.base_de_datos;

    opens Logico to javafx.graphics, javafx.fxml;
    exports Logico;
}
