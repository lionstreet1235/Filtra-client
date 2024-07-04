module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ui to javafx.fxml;
    exports com.example.ui;
    exports com.example.ui.controller;
    opens com.example.ui.controller to javafx.fxml;
}