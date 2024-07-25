    package com.example.ui.controller; // Correct package declaration

    import com.example.ui.model.User;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.stage.Stage;
    import com.google.gson.Gson;

    import javax.crypto.BadPaddingException;
    import javax.crypto.Cipher;
    import javax.crypto.IllegalBlockSizeException;
    import java.io.*;
    import java.net.Socket;
    import java.net.ConnectException;
    import java.time.LocalDateTime;
    import java.util.Base64;
    import java.util.UUID;

    import static com.example.ui.controller.ClientController.in;
    import static com.example.ui.controller.ClientController.out;

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
        protected static Cipher cipher;





        @FXML
        public void handleRegister() {
            String fullName = fullNameField.getText();
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match.");
                return;
            }
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showAlert("Error", "Invalid email format.");
                return;
            }
            try  {

                out.println("REG");
                String registerStatus = in.readLine();
                if (registerStatus.contains("logout")) {
                    Platform.runLater(()->{showAlert("Failure", "Please logout first!");});
                    return;
                }
                Gson gson = new Gson();
                byte[] username_byte = cipher.doFinal(username.getBytes());
                byte[] passwd_byte = cipher.doFinal(password.getBytes());
                String encrypted_username = Base64.getEncoder().encodeToString(username_byte);
                String encrypted_passwd = Base64.getEncoder().encodeToString(passwd_byte);
                User register_user = new User(UUID.randomUUID().toString(), fullName, encrypted_username, email, encrypted_passwd, LocalDateTime.now().toString(), true, false, 0);
                out.println(gson.toJson(register_user));

                Platform.runLater(() -> {
                    Platform.runLater(()->{showAlert("Success", registerStatus);});

                });

            } catch (ConnectException e) {
                Platform.runLater(() -> showAlert("Error", "Could not connect to the server."));
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Registration failed: " + e.getMessage()));
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
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
