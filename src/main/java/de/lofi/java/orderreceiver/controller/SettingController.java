package de.lofi.java.orderreceiver.controller;

import com.cathive.fonts.fontawesome.FontAwesomeIconView;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import de.lofi.java.orderreceiver.data.AppData;
import de.lofi.java.orderreceiver.model.Setting;
import de.lofi.java.orderreceiver.client.MailClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.mail.MessagingException;

public class SettingController {

    private Setting setting;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane settingBorderPane;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private TitledPane settingTitledPane;

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private CheckBox sslCheckBox;

    @FXML
    private TextField timeIntervallTextField;

    @FXML
    private CheckBox alertCheckBox;
    @FXML
    private CheckBox alarmCheckBox;

    @FXML
    private Label withSoundLabel;

    @FXML
    private Label soundFileLabel;

    @FXML
    private TextField soundFileTextField;

    @FXML
    private FontAwesomeIconView soundFileClearIcon;

    @FXML
    private Button testSoundButton;


    @FXML
    private CheckBox printCheckBox;

    @FXML
    private TextField printerTextField;

    @FXML
    private TextField bonWidthTextField;

    @FXML
    private TextField bonPaddingTextField;

    private boolean changed = false;

    @FXML
    private TextField subjectFilterTextField;

    @FXML
    private TextField senderFilterTextField;

    private BooleanProperty alertProperty = new SimpleBooleanProperty(false);

    private BooleanProperty printProperty = new SimpleBooleanProperty(false);

    @FXML
    void initialize() {
        setting = AppData.getInstance().getSetting();
        loadSettingToViews();
        bindViewsToSetting();
        setViewVisibility();
    }

    private void bindViewsToSetting()  {
        this.hostTextField.setOnKeyReleased( e -> {
            setting.setHost(this.hostTextField.getText());
            changed = true;
        });

        this.portTextField.setOnKeyReleased( e -> {
            setting.setPort(Integer.parseInt(this.portTextField.getText()));
            changed = true;
        });

        this.usernameTextField.setOnKeyReleased( e -> {
            setting.setUsername(this.usernameTextField.getText());
            changed = true;
        });

        this.passwordTextField.setOnKeyReleased( e -> {
            setting.setPassword(this.passwordTextField.getText());
            changed = true;
        });

        this.sslCheckBox.setOnAction( e -> {
            setting.setUseSSL(this.sslCheckBox.isSelected());
            changed = true;
        });

        this.timeIntervallTextField.setOnKeyReleased( e -> {
            setting.setTimeIntervall(Integer.parseInt(this.timeIntervallTextField.getText()));
            changed = true;
        });

        this.alertCheckBox.setOnAction( e -> {
            setting.setShowAlert(this.alertCheckBox.isSelected());
            this.alertProperty.setValue(this.alertCheckBox.isSelected());
            changed = true;
        });

        this.alarmCheckBox.setOnAction( e -> {
            setting.setSoundAlarm(this.alarmCheckBox.isSelected());
            changed = true;
        });

        this.soundFileTextField.textProperty().addListener( (e, oldValue, newValue) -> {
            if (oldValue != newValue) {
                setting.setSoundFile(newValue);
                changed = true;
            }
        });

        this.printCheckBox.setOnAction( e -> {
            setting.setAutoPrint(this.printCheckBox.isSelected());
            changed = true;
        });

        this.printerTextField.textProperty().addListener( (e, oldValue, newValue) -> {
            if (oldValue != newValue) {
                setting.setDefaultPrinter(newValue);
                changed = true;
            }
        });

        this.bonWidthTextField.setOnKeyReleased( e -> {
            setting.setTimeIntervall(Integer.parseInt(this.bonWidthTextField.getText()));
            changed = true;
        });

        this.bonPaddingTextField.setOnKeyReleased( e -> {
            setting.setTimeIntervall(Integer.parseInt(this.bonPaddingTextField.getText()));
            changed = true;
        });

        this.subjectFilterTextField.setOnKeyReleased( e -> {
            setting.setSubjectFilter(this.subjectFilterTextField.getText());
            changed = true;
        });

        this.senderFilterTextField.setOnKeyReleased( e -> {
            setting.setSenderFilter(this.senderFilterTextField.getText());
            changed = true;
        });

    }

    private void loadSettingToViews() {
        this.hostTextField.setText(setting.getHost());
        this.portTextField.setText(String.valueOf(setting.getPort()));
        this.usernameTextField.setText(setting.getUsername());
        this.passwordTextField.setText(String.valueOf(setting.getPassword()));
        this.sslCheckBox.setSelected(setting.isUseSSL());
        this.timeIntervallTextField.setText(String.valueOf(setting.getTimeIntervall()));
        this.alertCheckBox.setSelected(this.setting.isShowAlert());
        this.alertProperty.setValue(this.setting.isShowAlert());
        this.alarmCheckBox.setSelected(this.setting.isSoundAlarm());
        this.soundFileTextField.setText(this.setting.getSoundFile());
        this.printCheckBox.setSelected(this.setting.isAutoPrint());
        this.printerTextField.setText(this.setting.getDefaultPrinter());
        this.bonWidthTextField.setText(String.valueOf(this.setting.getBonWidth()));
        this.bonPaddingTextField.setText(String.valueOf(this.setting.getBonPadding()));
        this.subjectFilterTextField.setText(this.setting.getSubjectFilter());
        this.senderFilterTextField.setText(this.setting.getSenderFilter());
    }

    private void setViewVisibility() {
        this.withSoundLabel.visibleProperty().bind(this.alertProperty);
        this.alarmCheckBox.visibleProperty().bind(this.alertProperty);
        this.soundFileTextField.visibleProperty().bind(this.alertProperty);
        this.testSoundButton.visibleProperty().bind(this.alertProperty);
        this.soundFileLabel.visibleProperty().bind(this.alertProperty);
        this.soundFileClearIcon.visibleProperty().bind(this.alertProperty);
    }

    @FXML
    void onOpenFileButtonAction(ActionEvent event) {
        Stage stage = (Stage) settingBorderPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Datei-Öffnen");
        List<File> list =
                fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            soundFileTextField.setText(list.get(0).toURI().toString());
        }
    }

    @FXML
    void onTestSoundButtonAction(ActionEvent event) {
        if (this.soundFileTextField.getText().isEmpty()) {
            return;
        }
        try {
            Media pick = new Media(this.soundFileTextField.getText()); //throws here
            MediaPlayer player = new MediaPlayer(pick);
            player.play();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sound-Datei");
            alert.setContentText("Fehler beim Abspielen der Sound-Datei");
            alert.show();
        }
    }

    @FXML
    void onPrinterDialogAction(ActionEvent event) {
        ChoiceDialog dialog = new ChoiceDialog(Printer.getDefaultPrinter(), Printer.getAllPrinters());
        dialog.setHeaderText("Standard-Drucker für Bon-Ausdruck");
        //dialog.setContentText("Drucker");
        dialog.setTitle("Drucker-Auswahl");
        Optional<Printer> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            Printer printer = opt.get();
            this.printerTextField.setText(printer.getName());
        }
    }

    @FXML
    void onTestConnectionAction(ActionEvent event) {
        try {
            MailClient.testConnection(setting.getHost(), setting.getUsername(), setting.getPassword());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Verbindung wurde erfolgreich herstellt.");
            alert.setTitle("Verbinding Testen");
            alert.show();
        } catch (MessagingException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Verbinding Testen");
            alert.setContentText("Verbindung ist fehlgeschlagen.");
            alert.show();
        }
    }



    @FXML
    void onSoundFileTextFieldClicked(MouseEvent event) {
        Stage stage = (Stage) settingBorderPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Datei-Öffnen");
        List<File> list =
                fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            soundFileTextField.setText(list.get(0).toURI().toString());
        }
    }

    @FXML
    void onSoundFileClearIcon(MouseEvent event) {
        soundFileTextField.setText("");
    }



}
