package com.example.ui.controller;

import com.example.ui.HelloApplication;
import com.example.ui.model.SharedDataModel;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.ui.model.User;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientController {
    public  static String selectedFile4Share;
    private static final String SERVER_NAME = "localhost";
    private static final int DATA_PORT = 2000;
    private static final int CONTROL_PORT = 2100;
    public static Socket controlSocket = null;
    public ListView remoteFilesList;
    public ListView sharedFilesList;


    public static BufferedReader in = null;
    public static PrintWriter out = null;
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
    @FXML
    private Button removeButton;

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
    private ImageView usernameIcon;
    @FXML
    private ImageView fullnameIcon;
    @FXML
    private ImageView emailIcon;
    @FXML
    private ImageView dateCreatedIcon;
    @FXML
    private ImageView activatedIcon;
    static final Object pauseLock = new Object();
    static boolean isPaused = false;

    // Make dir view
    @FXML
    private Button makeDirButton;


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
                try{
                    Gson gson = new Gson();
                    user_login = gson.fromJson(login_status, User.class);
                    Platform.runLater(() -> showAlert("status", "login successful"));
                    Platform.runLater(()-> statusLabel.setText("Connected"));
                }catch (Exception e){
                    Platform.runLater(()->showAlert("status","User did not exist"));
                }




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

    //controller upload
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

            out.println("up " + fileName);
            out.println(uploadFile.length());




            new Thread(() -> {
                try (Socket dataSocket = new Socket(SERVER_NAME, DATA_PORT);
                     BufferedInputStream in = new BufferedInputStream(new FileInputStream(uploadFile)); BufferedOutputStream out = new BufferedOutputStream(dataSocket.getOutputStream()))
                {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1)
                    {
                        synchronized (pauseLock)
                        {
                            while (isPaused)
                            {
                                pauseLock.wait();
                            }
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    out.flush();
                    dataSocket.close();
                    Platform.runLater(()->showAlert("status", "File uploaded successfully!"));
                } catch (IOException | InterruptedException e)
                {
                    Platform.runLater(() -> showAlert("Error", "File upload failed: " + e.getMessage()));
                }
            }).start();
        }
    }
    @FXML
    private void downloadFile() {
        HBox selectedItem = (HBox) remoteFilesList.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Platform.runLater(() -> statusLabel.setText("Please select a file to download."));
            return;
        }

        Label label = (Label) selectedItem.getChildren().get(1); // assuming label is the second child in HBox
        String selectedFile = label.getText().substring(1).trim();

        new Thread(() -> {
            synchronized (out) {
                String downloaFile = selectedFile;
                out.println("get " + downloaFile);
                out.flush();
            }

            Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(selectedFile);
                Stage stage = (Stage) downloadButton.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    new Thread(() -> {
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
                            Platform.runLater(() -> statusLabel.setText("DOWNLOAD COMPLETE!"));
                        } catch (IOException e) {
                            System.out.println("File download error: " + e.getMessage());
                            Platform.runLater(() -> statusLabel.setText("DOWNLOAD FAILED!"));
                        }
                    }).start();
                }
            });
        }).start();
    }
    @FXML
    private void removeFileorDirectory() throws IOException {
        HBox selectedItem = (HBox) remoteFilesList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Platform.runLater(() -> statusLabel.setText("Please select a file or directory to remove"));
            return;
        }
        Label label = (Label) selectedItem.getChildren().get(1); // assuming label is the second child in HBox
        String selectedFile = label.getText().substring(1).trim();
        out.println("rm " + selectedFile);
        String remove_status = in.readLine();
        Platform.runLater(()->statusLabel.setText(remove_status+" "+selectedFile));
        showUserFile();
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
            // Icon cho show profile
            usernameIcon.setImage(new Image(getClass().getResourceAsStream("/image/user.png")));
            fullnameIcon.setImage(new Image(getClass().getResourceAsStream("/image/identity.png")));
            emailIcon.setImage(new Image(getClass().getResourceAsStream("/image/communication.png")));
            dateCreatedIcon.setImage(new Image(getClass().getResourceAsStream("/image/timetable.png")));
            activatedIcon.setImage(new Image(getClass().getResourceAsStream("/image/check.png")));

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
        if(logOut_status.contains("See you again ")) {
            Platform.runLater(this::setFieldNull);
            Platform.runLater(()-> remoteFilesList = null);
            Platform.runLater(this::showUserFile);
        }

    }



    @FXML
    private void showUserFile() {
        new Thread(() -> {
            try {
                out.println("LS");
                List<String> fileList = new ArrayList<>();
                String response_LS;
                while ((response_LS = in.readLine()) != null) {
                    if (response_LS.contains("Login")) {
                        System.out.println(response_LS);
                        break;
                    }
                    if (response_LS.equals("END")) {
                        break;
                    }
                    fileList.add(response_LS);
                }
                out.flush();
                if (user_login != null) {
                    Platform.runLater(() -> updateRemoteFilesList(fileList));
                } else {
                    Platform.runLater(() -> statusLabel.setText("No user logged in!"));
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    throw new RuntimeException(e);
                });
            }
        }).start();
    }


    private void updateRemoteFilesList(List<String> fileList) {
        remoteFilesList.getItems().clear();
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (String file : fileList) {
            HBox hBox = new HBox();
            ImageView icon = new ImageView();
            String iconPath = "";

            if (file.startsWith("D")) {
                iconPath = "/image/File_Explorer.png";
            } else if (file.startsWith("F")) {
                iconPath = "/image/Document.png";
            }

            try {
                Image image = new Image(getClass().getResourceAsStream(iconPath));
                icon.setImage(image);
                icon.setFitWidth(24);
                icon.setFitHeight(24);
                icon.setPreserveRatio(true);
            } catch (NullPointerException e) {
                System.err.println("Không thể tìm thấy hình ảnh: " + iconPath);

                icon.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                icon.setFitWidth(24);
                icon.setFitHeight(24);
                icon.setPreserveRatio(true);
            }

            // Tạo Label mà không có ký tự (F: hoặc D:)
            Label label = new Label(file.substring(1));
            hBox.getChildren().addAll(icon, label);
            items.add(hBox);
        }
        remoteFilesList.setItems(items);
    }

    @FXML
    private void showSharedFiles() throws IOException {
        out.println("LSHR");
        List<String> fileList = new ArrayList<>();
        String status = in.readLine();
        System.out.println(status);
        if (status.contains("Login")) {
            return;
        }

        Gson gson = new Gson();
        String[] list_file = gson.fromJson(in.readLine(), String[].class);
        String[] list_dir = gson.fromJson(in.readLine(), String[].class);

        for (String dir : list_dir) {
            fileList.add(dir);
        }
        for (String file : list_file) {
            fileList.add(file);
        }

        // Cập nhật danh sách tệp chia sẻ trên giao diện người dùng
        Platform.runLater(() -> updateSharedFilesList(fileList));
    }

    private void updateSharedFilesList(List<String> fileList) {
        sharedFilesList.getItems().clear();
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (String file : fileList) {
            HBox hBox = new HBox();
            ImageView icon = new ImageView();
            String iconPath = "";

            if (file.startsWith("D")) {
                iconPath = "/image/File_Explorer.png";
            } else if (file.startsWith("F")) {
                iconPath = "/image/Document.png";
            }

            try {
                InputStream iconStream = getClass().getResourceAsStream(iconPath);
                if (iconStream == null) {
                    throw new NullPointerException("Không thể tìm thấy hình ảnh: " + iconPath);
                }
                Image image = new Image(iconStream);
                icon.setImage(image);
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
                icon.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }

            icon.setFitWidth(24);
            icon.setFitHeight(24);
            icon.setPreserveRatio(true);

            // Tạo Label mà không có ký tự (F: hoặc D:)
            Label label = new Label(file.substring(1));
            hBox.getChildren().addAll(icon, label);
            items.add(hBox);
        }
        sharedFilesList.setItems(items);
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
    @FXML
    private void showMakeDirForm(ActionEvent actionEvent){
        try{
            if(user_login!=null){
                FXMLLoader fxmloader = new FXMLLoader(HelloApplication.class.getResource("make-dir.fxml"));
                Parent root = fxmloader.load();

                Stage mkDirStage = new Stage();
                mkDirStage.setTitle("MakeDirectory");
                mkDirStage.setScene(new Scene(root));
                mkDirStage.show();


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @FXML
    private void showShareFileForm(ActionEvent actionEvent){
        try{
            if(user_login!=null){
                HBox selectedItem = (HBox) remoteFilesList.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    Platform.runLater(() -> statusLabel.setText("Please select a file to download."));
                    return;
                }

                Label label = (Label) selectedItem.getChildren().get(1); // assuming label is the second child in HBox
                String selectedFile = label.getText().substring(1).trim();
                SharedDataModel.setSelectedFileForShare(selectedFile);// truyen qua model de co the lay ra o class khac
                FXMLLoader fxmloader = new FXMLLoader(HelloApplication.class.getResource("share-view.fxml"));
                Parent root = fxmloader.load();

                Stage mkDirStage = new Stage();
                mkDirStage.setTitle("Share files");
                mkDirStage.setScene(new Scene(root));
                mkDirStage.show();
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void getSharedFiles(ActionEvent actionEvent) {
        try{
            HBox selectedItem = (HBox) sharedFilesList.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                Platform.runLater(() -> statusLabel.setText("Please select a file to download."));
                return;
            }

            Label label = (Label) selectedItem.getChildren().get(1); // assuming label is the second child in HBox
            String input = label.getText();
            String selectedSharedFile = input.substring(input.indexOf("F:") + 3, input.indexOf("FROM:")).trim();
            String selectedSharedEmail = input.substring(input.indexOf("FROM:")+5).trim();
            System.out.println(selectedSharedEmail);
            System.out.println(selectedSharedFile);

            new Thread(() -> {
                synchronized (out) {
                    String downloadSharedFile = selectedSharedFile;
                    String sharedEmail = selectedSharedEmail;
                    out.println("getfs " +sharedEmail+" "+ downloadSharedFile);
                    out.flush();
                }

                Platform.runLater(() -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialFileName(selectedSharedFile);
                    Stage stage = (Stage) downloadButton.getScene().getWindow();
                    File file = fileChooser.showSaveDialog(stage);

                    if (file != null) {
                        new Thread(() -> {
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
                                Platform.runLater(() -> statusLabel.setText("DOWNLOAD COMPLETE!"));
                            } catch (IOException e) {
                                System.out.println("File download error: " + e.getMessage());
                                Platform.runLater(() -> statusLabel.setText("DOWNLOAD FAILED!"));
                            }
                        }).start();
                    }
                });
            }).start();
        } catch (Exception e) {
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
