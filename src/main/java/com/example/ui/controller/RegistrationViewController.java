    package com.example.ui.controller; // Correct package declaration

    import com.example.ui.model.User;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.stage.Stage;
    import com.google.gson.Gson;

    import java.io.*;
    import java.net.Socket;
    import java.net.ConnectException;
    import java.time.LocalDateTime;
    import java.util.UUID;

    public class RegistrationViewController {
        public static User user;
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



                out.println("REG"); // Registration command
                String registerStatus = in.readLine();
                if (registerStatus.contains("logout")) {
                    Platform.runLater(()->{showAlert("Failure", "Please logout first!");});
                    return;
                }
                Gson gson = new Gson();
                user = new User(UUID.randomUUID().toString(), fullName, username, email, password, LocalDateTime.now().toString(), true, false, 2);
                out.println(gson.toJson(user));


                // Get the server's response
                Platform.runLater(() -> {
                    Platform.runLater(()->{showAlert("Success", registerStatus);});

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
