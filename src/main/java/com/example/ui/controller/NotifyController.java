package com.example.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.example.ui.controller.ClientController.in;
import static com.example.ui.controller.ClientController.out;

public class NotifyController {

    @FXML
    private Text notiList;

    @FXML
    private void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        Thread thread = new Thread(() -> {
            try {
                if (in == null) {
                    Platform.runLater(() -> showAlert("Error", "Not connected to server"));
                    return;
                }


                out.println("noti");
                out.flush();


                while (in.readLine()!= null){
                    String notificationsString = in.readLine();
                    notificationsString = notificationsString.substring(1, notificationsString.length() - 1);
                    List<String> notifications = Arrays.stream(notificationsString.split(",\\s*"))
                                .map(s -> s.replaceAll("^\"|\"$", "")) // Remove quotes
                                .map(s -> s.replaceAll("\\\\u0027", "'")) // Replace unicode for single quote
                                .collect(Collectors.toList());


                    String notificationText = String.join("\n", notifications);
                    notiList.setText(notificationText);
                    in.reset();
                    }



            } catch (IOException e) {
                System.out.println("reading");
            }
        });
        thread.start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
