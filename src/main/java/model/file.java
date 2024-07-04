package model;

public class file {
    private String fileName;
    private String filePath;
    private String fileType;
    private int fileid;

    public file(String fileName, String filePath, String fileType, int fileid) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileid = fileid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getFileid() {
        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
    }
}
