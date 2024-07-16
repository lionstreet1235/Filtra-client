package com.example.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;

import static com.example.ui.controller.ClientController.in;
import static com.example.ui.controller.ClientController.out;

public class MakeDirController {

    public Button createDirButton;
    @FXML
    private TextField dirTextField;

    @FXML
    private void makeDir() {
        try {

            String dirName = dirTextField.getText();
            out.println("mkdir " + dirName);
            String status = in.readLine();
            Platform.runLater(() -> {
                showAlert("status", status);
            });


        } catch (ConnectException e) {
            Platform.runLater(() -> showAlert("Error", "Could not connect to the server."));
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error", "Could not open the connection to the server."));
        }


    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
