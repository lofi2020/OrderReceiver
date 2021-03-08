package de.lofi.java.orderreceiver.controller;

import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import de.lofi.java.orderreceiver.app.Constants;
import de.lofi.java.orderreceiver.data.AppData;
import de.lofi.java.orderreceiver.data.OrderManager;
import de.lofi.java.orderreceiver.data.SettingManager;
import de.lofi.java.orderreceiver.model.FileInfo;
import de.lofi.java.orderreceiver.model.OrderInfo;
import de.lofi.java.orderreceiver.service.OrderUpdateService;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.w3c.dom.Document;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderBrowserController {

    AppData appData = AppData.getInstance();

    OrderManager orderManager = new OrderManager();

    SettingManager settingManager = new SettingManager();

    OrderUpdateService orderUpdateService = new OrderUpdateService();

    Timeline updateServiceTimeline;

    MediaPlayer mediaPlayer = null;

    BorderPane settingViewPane = null;

    private BooleanProperty isListEditing = new SimpleBooleanProperty(false);

    private BooleanProperty isSetting = new SimpleBooleanProperty(false);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<OrderInfo> orderListView;

    @FXML
    private Button listEditButton;

    @FXML
    private Button selectAllButton;

    @FXML
    private Button orderDeleteButton;

    @FXML
    private AnchorPane contentAnchorPane;

    @FXML
    private SplitPane orderSplitPane;

    @FXML
    private BorderPane orderViewBorderPane;

    @FXML
    private Label orderViewTitle;

    @FXML
    private FontAwesomeIconView printIcon;

    @FXML
    private FontAwesomeIconView settingIcon;

    @FXML
    private FontAwesomeIconView windowCloseIcon;

    @FXML
    private FontAwesomeIconView windowMinimizeIcon;

    @FXML
    private FontAwesomeIconView windowMaximizeIcon;


    @FXML
    private HBox appToolBar;

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<FileInfo> fileListView;

    @FXML
    private WebView webView;

    @FXML
    private Label orderListTitle;

    private FileInfo currentFileInfo;

    @FXML
    void initialize() {
        initViews();
        initServices();
    }

    private void initViews() {
        this.orderDeleteButton.visibleProperty().bind(isListEditing);
        this.selectAllButton.visibleProperty().bind(isListEditing);
        printIcon.visibleProperty().bind(isSetting.not());

        orderListView.setCellFactory(new Callback<ListView<OrderInfo>,
                                             ListCell<OrderInfo>>() {
                                         public OrderListCell call(ListView<OrderInfo> list) {
                                             return new OrderListCell();
                                         }
                                     }
        );

        orderListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        var orderInfo = orderListView.getSelectionModel().getSelectedItems().get(0);
                        showOrder(orderInfo);
                    }
                });

        fileListView.setCellFactory(new Callback<ListView<FileInfo>,
                                            ListCell<FileInfo>>() {
                                        public FileListCell call(ListView<FileInfo> list) {
                                            return new FileListCell();
                                        }
                                    }
        );

        fileListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        currentFileInfo = fileListView.getSelectionModel().getSelectedItems().get(0);
                        showFileContent();
                    }
                });

        updateOrderList();
        this.orderViewBorderPane.setVisible(false);

    }

    private void updateOrderList() {
        ObservableList orderList = FXCollections.observableArrayList(appData.getOrderInfoList().getOrderInfos().toArray());
        orderListView.setItems(orderList);
        orderListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        orderListTitle.setText(String.format("Bestellungen (%s)", appData.getOrderInfoList().getOrderInfos().size()));
    }

    private void showStatusLabel(String status) {
        this.statusLabel.setText(status);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), this.statusLabel);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void initServices() {
        orderUpdateService.setOnSucceeded(e -> {
            showStatusLabel(this.orderUpdateService.getValue());
            if (orderUpdateService.getOrderInfos().size() > 0) {
                orderManager.addOrderInfos(orderUpdateService.getOrderInfos());
                updateOrderList();
                if (appData.getSetting().isShowAlert()) {
                    showNewOrderAlertAndPlayAlarmSound();
                }
            }
        });
        startUpdateService();
    }

    private void startUpdateService() {
        updateServiceTimeline = new Timeline(new KeyFrame(Duration.millis(appData.getSetting().getTimeIntervall() * 60000), e -> orderUpdateService.restart()));
        updateServiceTimeline.setCycleCount(Animation.INDEFINITE);
        updateServiceTimeline.play();
    }

    private void stopUpdateService() {
        updateServiceTimeline.stop();
    }

    private void showOrder(OrderInfo orderInfo) {
        var mainFileInfo = orderInfo.getFileInfos().get(0);
        currentFileInfo = mainFileInfo;
        showFileContent();
        fileListView.setItems(FXCollections.observableArrayList(orderInfo.getFileInfos()));
        fileListView.getSelectionModel().select(mainFileInfo);
    }

    private void showFileContent() {
        if (isSetting.getValue()) {
            this.changeSettingState();
        }
        String content = orderManager.loadFileContent(currentFileInfo.getFilePath());
        this.webView.getEngine().loadContent(content);
        this.orderViewTitle.setText(currentFileInfo.getTitle());
        orderViewBorderPane.setVisible(true);

        this.webView.getEngine().documentProperty().addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
                String heightText = webView.getEngine().executeScript(
                        "window.getComputedStyle(document.body, null).getPropertyValue('height')"
                ).toString();
                String widthText = webView.getEngine().executeScript(
                        "window.getComputedStyle(document.body, null).getPropertyValue('width')"
                ).toString();
                double height = Double.valueOf(heightText.replace("px", ""));
                webView.setPrefHeight(height);
            }
        });
    }

    @FXML
    void onListEditButtonAction(ActionEvent event) {
        isListEditing.setValue(!isListEditing.getValue());
        if (isListEditing.getValue()) {
            listEditButton.setText("Abbrechen");
        } else {
            listEditButton.setText("Bearbeiten");
        }
    }

    @FXML
    void onPrintIconAction(MouseEvent event) {
      printFile(currentFileInfo);
    }

    @FXML
    void onSelectAllButtonAction(ActionEvent event) {
        if (orderListView.getSelectionModel().getSelectedItems().size() == 0) {
            orderListView.getSelectionModel().selectAll();
            System.out.println("Seleted Size: " + orderListView.getSelectionModel().getSelectedItems().size());
            selectAllButton.setText("Alles Abwählen");
        } else {
            orderListView.getSelectionModel().clearSelection();
            selectAllButton.setText("Alles Auswählen");
        }
        orderListView.refresh();
    }

    @FXML
    void onSettingIconAction(MouseEvent event) {
        if (this.settingViewPane == null) {
            try {
                settingViewPane = (BorderPane) FXMLLoader.load(getClass().getResource("../view/SettingView.fxml"));
                settingViewPane.setPrefWidth(contentAnchorPane.getWidth());

                settingViewPane.setPrefHeight(contentAnchorPane.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        changeSettingState();
    }

    @FXML
    void onRefreshIconAction(MouseEvent event) {
        this.orderUpdateService.restart();
    }

    @FXML
    void onOrderDeleteButton(ActionEvent event) {
        var orderInfos = orderListView.getSelectionModel().getSelectedItems();
        if (orderInfos.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Constants.MESSAGE_TITLE_DELETE_ORDER);
            alert.setContentText(Constants.MESSAGE_TEXT_SELECT_A_ORDER_TO_DELETE);
            alert.show();
            return;
        }

        if (orderInfos.size() < appData.getOrderInfoList().getOrderInfos().size()) {
            orderManager.removeOrders(orderInfos);
        } else {
            orderManager.removeAll();
        }
        orderViewBorderPane.setVisible(false);
        updateOrderList();
    }

    private void changeSettingState() {
        if (!isSetting.getValue()) {
            stopUpdateService();
            settingIcon.setStyle("-fx-text-fill: black;");
            contentAnchorPane.getChildren().remove(orderSplitPane);
            contentAnchorPane.getChildren().add(settingViewPane);
        } else {
            contentAnchorPane.getChildren().remove(settingViewPane);
            contentAnchorPane.getChildren().add(orderSplitPane);
            settingIcon.setStyle("-fx-text-fill: white;");
            settingManager.saveSettings();
            startUpdateService();
        }
        isSetting.setValue(!isSetting.getValue());
    }

    private void showNewOrderAlertAndPlayAlarmSound() {

        if (appData.getSetting().isSoundAlarm()) {
            try {
                Media pick = new Media(appData.getSetting().getSoundFile()); //throws here
                mediaPlayer = new MediaPlayer(pick);
                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.seek(Duration.ZERO);
                        mediaPlayer.play();
                    }
                });
                mediaPlayer.play();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Constants.MESSAGE_TITLE_NEW_ORDERS);
        alert.setContentText(String.format(Constants.MESSAGE_TEXT_NEW_ORDERS, orderUpdateService.getOrderInfos().size()));
        alert.showAndWait().ifPresent((btnType) -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

    }

    @FXML
    void onPrintAllFilesIconAction(MouseEvent event) {
       printAllFiles(this.orderListView.getSelectionModel().getSelectedItems().get(0));
    }


    @FXML
    void onWindowCloseIconAction(MouseEvent event) {
        Stage obj = (Stage) windowCloseIcon.getScene().getWindow();
        obj.close();
    }

    @FXML
    void onWindowMaximizeIcon(MouseEvent event) {
        Stage obj = (Stage) windowMaximizeIcon.getScene().getWindow();
        if (obj.isMaximized()) {
            obj.setMaximized(false);
            this.windowMaximizeIcon.setStyle("-fx-text-fill: white");
        } else {
            this.windowMaximizeIcon.setStyle("-fx-text-fill: blue");
            obj.setMaximized(true);
        }
    }

    @FXML
    void onWindowsMinimizeIconAction(MouseEvent event) {
        Stage obj = (Stage) windowMinimizeIcon.getScene().getWindow();
        obj.setIconified(true);
    }

    private void printAllFiles(OrderInfo orderInfo) {
        for(FileInfo fileInfo : orderInfo.getFileInfos()) {
            printFile(fileInfo);
        }
    }

    private void printFile(FileInfo fileInfo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        Printer defaultPrinter = Printer.getDefaultPrinter();

        //Search printer by name
        /*
        Printer defaultPrinter = null;
         var iter = Printer.getAllPrinters().iterator();
         while(iter.hasNext()) {
             var printer = iter.next();
             if (printer.getName().toLowerCase().contains(appData.getSetting().getDefaultPrinter().toLowerCase())) {
                defaultPrinter = printer;
             }
         }

         */

        WebView printWebView = new WebView();
        //WebView printWebView = this.webView;

        String content = OrderManager.loadFileContent(fileInfo.getFilePath());

        var paper = job.getJobSettings().getPageLayout().getPaper();
        final PageLayout pageLayout = defaultPrinter.createPageLayout(paper,
                PageOrientation.LANDSCAPE,
                Printer.MarginType.DEFAULT);

        final double scaleX = pageLayout.getPrintableWidth() / printWebView.getWidth();
        final double scaleY = pageLayout.getPrintableHeight() / printWebView.getHeight();
        final double scale = Math.min(scaleX, scaleY);
        //printWebView.getTransforms().add(new Scale(1.5, 1.5));

        if (job != null && job.showPageSetupDialog(webView.getScene().getWindow())) {
            //if (job != null) {
            System.out.println("Starting print webview");
            printWebView.getEngine().loadContent(content);
            printWebView.getEngine().print(job);
            job.endJob();
        }
    }
}
