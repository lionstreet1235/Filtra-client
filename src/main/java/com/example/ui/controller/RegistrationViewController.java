package com.example.ui.controller; // Correct package declaration

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.net.ConnectException;

public class RegistrationViewController {
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;

    private final String SERVER_NAME = "localhost";
    private final int CONTROL_PORT = 2100;

    // Reference to the main controller (if needed for communication)
    private ClientController mainController;

    public void setMainController(ClientController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleRegister() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Enhanced Input Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Email format validation (add more robust validation if needed)
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "Invalid email format.");
            return;
        }

        try (Socket registrationSocket = new Socket(SERVER_NAME, CONTROL_PORT);
             PrintWriter out = new PrintWriter(registrationSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(registrationSocket.getInputStream()))) {

            // Send registration data to the server
            out.println("REG"); // Registration command
            out.println(fullName);
            out.println(username);
            out.println(email);
            out.println(password);

            // Get the server's response
            String response = in.readLine();
            Platform.runLater(() -> {
                if (response.startsWith("Registration successful")) {
                    showAlert("Success", response);

                    // Optionally, automatically log the user in after successful registration
                    // mainController.loginAfterRegistration(username, password);
                } else {
                    showAlert("Success", response);
                }
            });

        } catch (ConnectException e) {
            Platform.runLater(() -> showAlert("Error", "Could not connect to the server."));
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error", "Registration failed: " + e.getMessage()));
        }

        // Close the registration window after registration or error
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
