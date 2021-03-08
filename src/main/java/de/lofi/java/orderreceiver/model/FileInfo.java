package de.lofi.java.orderreceiver.model;

public class FileInfo {

    public enum  FileInfoType { ORDER, BILL, BONS };

    private String title;
    private String filePath;
    private FileInfoType type = FileInfoType.ORDER;

    public FileInfo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileInfoType getType() {
        return type;
    }

    public void setType(FileInfoType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "title='" + title + '\'' +
                ", filePath='" + filePath + '\'' +
                ", type=" + type +
                '}';
    }
}
