module com.example.javafxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.javafxapp.model to javafx.base;
    opens com.example.javafxapp to javafx.fxml;
    exports com.example.javafxapp;
}