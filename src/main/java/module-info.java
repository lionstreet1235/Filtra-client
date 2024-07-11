module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.ui to javafx.fxml;
    opens com.example.ui.model to com.google.gson; // Add this line

    exports com.example.ui;
    exports com.example.ui.controller;
    opens com.example.ui.controller to javafx.fxml;
}
