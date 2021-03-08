package de.lofi.java.orderreceiver.controller;

import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import de.lofi.java.orderreceiver.model.FileInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

public class FileListCell extends ListCell<FileInfo> {

    private final String[] iconTextFillColors = {"blue", "red", "green"};

    @FXML
    private VBox fileVBox;

    @FXML
    private Label fileLabel;

    @FXML
    private FontAwesomeIconView fileIcon;

    FXMLLoader mLLoader;

    public FileListCell() {
        super();
    }

    @Override
    protected void updateItem(FileInfo fileInfo, boolean empty) {
        super.updateItem(fileInfo, empty);

        if (empty || fileInfo == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("../view/FileListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            this.fileLabel.setText(String.valueOf(fileInfo.getTitle()));
            this.fileIcon.setStyle(String.format("-fx-text-fill: %s;", iconTextFillColors[fileInfo.getType().ordinal()]));
            this.fileIcon.setFont(new Font(40.0));
            setText(null);
            setFont(Font.font(40));
            setTooltip(new Tooltip(fileInfo.getTitle()));
            setGraphic(fileVBox);
        }

    }


}
