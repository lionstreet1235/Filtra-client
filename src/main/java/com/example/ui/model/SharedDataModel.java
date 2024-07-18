package com.example.ui.model;

public class SharedDataModel {
    private static String selectedFileForShare;

    public static String getSelectedFileForShare() {
        return selectedFileForShare;
    }

    public static void setSelectedFileForShare(String selectedFileForShare) {
        SharedDataModel.selectedFileForShare = selectedFileForShare;
    }
}
