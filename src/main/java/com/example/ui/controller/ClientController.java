package com.example.ui.controller;

import com.example.ui.HelloApplication;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.ui.model.User;
import java.io.*;
import java.net.Socket;
public class ClientController {

    private static final String SERVER_NAME = "localhost";
    private static final int DATA_PORT = 2000;
    private static final int CONTROL_PORT = 2100;
    public static Socket controlSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private static User user_login;


    @FXML
    private TextField serverField;
    @FXML
    private TextField portField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Button uploadButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button showFilesButton;

    // View Show Profile
    @FXML
    private Text unameProfilefield;
    @FXML
    private Text emailfield;
    @FXML
    private Text fullnamefield;
    @FXML
    private Text activefield;
    @FXML
    private Text activatedfiled;


    @FXML
    private void initialize() throws IOException {
        connectButton.setOnAction(event -> connectToServer());
        uploadButton.setOnAction(event -> browseFileNew());
        uploadButton.setDisable(false);
        downloadButton.setDisable(false);
        showFilesButton.setDisable(false);
        controlSocket = new Socket(SERVER_NAME, CONTROL_PORT);
        in = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        out = new PrintWriter(controlSocket.getOutputStream(), true);

    }

    @FXML
    private void connectToServer() {

        String server = serverField.getText();
        int port = Integer.parseInt(portField.getText());
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            Platform.runLater(() -> statusLabel.setText("Username and password are required"));
            return;
        }

        new Thread(() -> {
            try {

                controlSocket = new Socket(server, port);
                in = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
                out = new PrintWriter(controlSocket.getOutputStream(), true);


                out.println("LOG");
                String response = in.readLine();
                if (response.contains("You need to logout first!")) {
                    return;
                }

                out.println(username);
                out.println(password);
                String login_status = in.readLine();
                if(response.contains("-- LOGIN --")) {
                    Platform.runLater(() -> statusLabel.setText("Loggin successful!"));
                }
                Gson gson = new Gson();
                user_login = gson.fromJson(login_status, User.class);


            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Connection failed: " + e.getMessage()));
            }
        }).start();
    }



    @FXML
    public void showRegistrationWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("RegistrationView.fxml"));
            Parent root = fxmlLoader.load();

            Stage registrationStage = new Stage();
            registrationStage.setTitle("Register");
            registrationStage.setScene(new Scene(root));
            registrationStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void browseFileNew() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String fileName = file.getName();
            File uploadFile = new File(file.getAbsolutePath());

            synchronized (out) {
                out.println("UP");
                out.println("up " + fileName);
            }

            new Thread(() -> {
                try (Socket dataSocket = new Socket(SERVER_NAME, DATA_PORT);
                     BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(uploadFile));
                     BufferedOutputStream dataOut = new BufferedOutputStream(dataSocket.getOutputStream())) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, bytesRead);
                    }
                    dataOut.flush();

                    // Ensure thread-safe access to in
                    String response;
                    synchronized (in) {
                        response = in.readLine();
                    }

                    if (response.startsWith("STARTING UPLOAD ...")) {
                        Platform.runLater(() -> statusLabel.setText("UPLOAD COMPLETE!"));
                    }

                } catch (IOException e) {
                    System.out.println("File upload error: " + e.getMessage());
                    Platform.runLater(() -> statusLabel.setText("UPLOAD FAILED!"));
                }
            }).start();
        }
    }


    @FXML
    private void showInfo(){
        out.println("INFO");
        if(user_login!= null){
            unameProfilefield.setText(user_login.getUsername());
            fullnamefield.setText(user_login.getFullname());
            emailfield.setText(user_login.getEmail());
            activefield.setText(user_login.getDate_created());
            if(user_login.activatedToString().contains("true")){
                activatedfiled.setText("Activated");
            }else {
                activatedfiled.setText("Not Activated");
            }

        }else {
            Platform.runLater(()-> statusLabel.setText("No user logged in!"));
        }




    }
    private void setFieldNull(){
        unameProfilefield.setText("");
        fullnamefield.setText("");
        emailfield.setText("");
        activefield.setText("");
        activatedfiled.setText("");
    }
    @FXML
    private void logOut() throws IOException {
        out.println("OUT");
        user_login = null;
        String logOut_status = in.readLine();
        Platform.runLater(()-> statusLabel.setText(logOut_status));
        if(logOut_status.contains("See you again ")){
            Platform.runLater(()-> setFieldNull());
          
        }

    }



    @FXML
    private void showUserFile() {

    }


    @FXML
    public void showActiveWindow(ActionEvent actionEvent) {
        try {
            if(user_login!=null){
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("otp-view.fxml"));
                Parent root = fxmlLoader.load();

                Stage registrationStage = new Stage();
                registrationStage.setTitle("Register");
                registrationStage.setScene(new Scene(root));
                registrationStage.show();
                out.println("OTP");
            }else Platform.runLater(()->statusLabel.setText("No user logged in!"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
