package com.example.ui.controller;

import com.example.ui.model.SharedDataModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import static com.example.ui.controller.ClientController.out;



public class ShareViewController {
    public TextField emailField;
    public Button otpBtn;
    public CheckBox checkBoxFull;
    public CheckBox checkBoxRead;
    public CheckBox checkBoxWrite;
    private String Message;

    @FXML
    private void ShareFile(){
        try{
            String selectedFile = SharedDataModel.getSelectedFileForShare();
            String email = emailField.getText();
            out.println("shr -e "+email+" -p "+Message+" -f "+selectedFile);
            Platform.runLater(() -> {showAlert("Share status", selectedFile+" has been shared to "+ email);});


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void setFullMessage(){
        Message = "FULL";
    }
    @FXML
    private void setReadMessage(){
        Message = "READ";
    }
    @FXML
    private void setWriteMessage(){
        Message = "WRITE";
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
