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
import java.net.Socket;

public class OtpViewController {
    private final String SERVER_NAME = "localhost";
    private final int CONTROL_PORT = 2100;


    @FXML
    private TextField otpField;
    @FXML
    private Button otpBtn;

    @FXML
    private void handleOtpButtonAction(){
        try (
             PrintWriter out = new PrintWriter(ClientController.controlSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(ClientController.controlSocket.getInputStream()))) {


            String activateStatus = in.readLine();
            if (activateStatus.contains("nope")) {
                String otp = otpField.getText();
                out.println(otp);
                String otpStatus = in.readLine();
                Platform.runLater(() -> {showAlert("ActiveStatus",otpStatus);});
            }

        } catch (ConnectException e) {
            Platform.runLater(() -> showAlert("Error", "Could not connect to the server."));
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error", "Registration failed: " + e.getMessage()));
        }

        // Close the registration window after registration or error
        Platform.runLater(() -> {
            Stage stage = (Stage) otpBtn.getScene().getWindow();
            stage.close();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
