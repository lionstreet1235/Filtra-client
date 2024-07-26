package com.example.ui.controller;

import com.example.ui.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.google.gson.Gson;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.example.ui.controller.ClientController.in;
import static com.example.ui.controller.ClientController.out;

public class RegistrationViewController implements Initializable {
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
    protected static Cipher cipher;
    private static final String AES_ALGO = "AES";
    private static final String secret_key = "tnqa_osint_ninja";
    protected static final SecretKeySpec secretKeySpec = new SecretKeySpec(secret_key.getBytes(), AES_ALGO);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            cipher = Cipher.getInstance(AES_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        new Thread(() -> {
            try {
                out.println("REG");
                String registerStatus = in.readLine();
                if (registerStatus.contains("logout")) {
                    Platform.runLater(() -> showAlert("Failure", "Please logout first!"));
                    return;
                }

                Gson gson = new Gson();
                byte[] usernameByte = cipher.doFinal(username.getBytes());
                byte[] passwdByte = cipher.doFinal(password.getBytes());
                String encryptedUsername = Base64.getEncoder().encodeToString(usernameByte);
                String encryptedPasswd = Base64.getEncoder().encodeToString(passwdByte);
                User registerUser = new User(UUID.randomUUID().toString(), fullName, encryptedUsername, email, encryptedPasswd, LocalDateTime.now().toString(), true, false, 0);
                out.println(gson.toJson(registerUser));

                Platform.runLater(() -> showAlert("Success", registerStatus));
            } catch (ConnectException e) {
                Platform.runLater(() -> showAlert("Error", "Could not connect to the server."));
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Registration failed: " + e.getMessage()));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
