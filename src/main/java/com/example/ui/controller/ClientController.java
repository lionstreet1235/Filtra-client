package com.example.ui.controller;

import com.example.ui.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController {

    private static final String SERVER_NAME = "localhost";
    private static final int DATA_PORT = 2000;
    private static final int CONTROL_PORT = 2100;
    private Socket controlSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private List<String> fileListFromServer;


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
    private ListView<String> localFilesList;

    @FXML
    private ListView<String> remoteFilesList;

    @FXML
    private Button uploadButton;

    @FXML
    private Button downloadButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button helpButton;
    @FXML
    private Button showFilesButton;

    @FXML
    private void initialize() throws IOException {
        connectButton.setOnAction(event -> connectToServer());
        uploadButton.setOnAction(event -> browseFileNew());

        profileButton.setOnAction(event -> showProfile());
        helpButton.setOnAction(event -> showHelp());
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

        new Thread(() -> {
            try {
                controlSocket = new Socket(server, port);
                in = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
                out = new PrintWriter(controlSocket.getOutputStream(), true);

                // Send "LOG" command to the server with username and password as arguments
                sendCommand("LOG", username, password);


            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Connection failed: " + e.getMessage()));
            }
        }).start();
    }

    private void sendCommand(String command, String... args) {
        new Thread(() -> {
            try {
                out.println(command);
                for (String arg : args) {
                    out.println(arg);
                }
                String response = in.readLine();
                Platform.runLater(() -> {
                    switch (command) {
                        case "LOG":
                            if (response.startsWith("LOGIN SUCCESSFUL")) {
                                statusLabel.setText(response);
                                uploadButton.setDisable(false);
                                downloadButton.setDisable(false);
                                showUserFile();
                            } else {
                                statusLabel.setText("Login status: " + response);
                            }
                            break;
                        case "UP":
                            if (response.startsWith("FILE UPLOADED SUCCESSFUL")) {
                                statusLabel.setText(response);
                            } else {
                                statusLabel.setText("Upload status: " + response);
                            }
                            break;
                        case "REG":
                            statusLabel.setText(response);
                            break;
                        default:
                            statusLabel.setText("Server response: " + response);
                            break;
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Command failed: " + e.getMessage()));
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


        sendCommand("UP");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            File uploadFile = new File(filePath);
            System.out.println(fileName);
            out.println(fileName);



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
                    dataSocket.close();

                    String response = in.readLine();
                    if(response.startsWith("STARTING UPLOAD ...")){
                        Platform.runLater(() -> statusLabel.setText("UPLOAD COMPLETE ! "));
                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        }
    }

    @FXML
    private void downloadFile() {
        String selectedFile = remoteFilesList.getSelectionModel().getSelectedItem().substring(5).trim();
        if (selectedFile == null) {
            statusLabel.setText("No file selected!");
            return;
        }

        new Thread(() -> {
            try {
                // Send "GET" command to the server with the selected file name
                out.println("GET");
                out.println(selectedFile);

                String serverResponse = in.readLine();
                if (serverResponse.contains("STARTING")) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialFileName(selectedFile);
                    Platform.runLater(() -> {
                        Stage stage = (Stage) downloadButton.getScene().getWindow();
                        File file = fileChooser.showSaveDialog(stage);

                        if (file != null) {
                            try (Socket dataSocket = new Socket(SERVER_NAME, DATA_PORT);
                                 BufferedInputStream dataIn = new BufferedInputStream(dataSocket.getInputStream());
                                 BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file))) {

                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = dataIn.read(buffer)) != -1) {
                                    fileOut.write(buffer, 0, bytesRead);
                                }

                                fileOut.flush();
                                dataSocket.close();
                                Platform.runLater(() -> statusLabel.setText("File downloaded successfully!"));

                            } catch (IOException e) {
                                Platform.runLater(() -> statusLabel.setText("Download failed: " + e.getMessage()));
                            }
                        }
                    });
                } else {
                    Platform.runLater(() -> statusLabel.setText("Server response: " + serverResponse));
                }
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Download failed: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void showUserFile() {
        new Thread(() -> {
            try {
                sendCommand("ls");
                fileListFromServer = new ArrayList<>();
                String responseList;
                while ((responseList = in.readLine()) != null) {
                    if (responseList.equals("EXIT")) {
                        break;
                    }
                    if (responseList.equals("REQUIRED")) {
                        break;
                    }
                    fileListFromServer.add(responseList);
                }
                Platform.runLater(() -> receiveFileListFromServer(fileListFromServer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void receiveFileListFromServer(List<String> response) {
        remoteFilesList.getItems().setAll(response);
    }
    private void showProfile() {
        statusLabel.setText("Profile button clicked!");
    }

    private void showHelp() {
        statusLabel.setText("Help button clicked!");
    }
}
