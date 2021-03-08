package de.lofi.java.orderreceiver.controller;


import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import de.lofi.java.orderreceiver.model.OrderInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class OrderListCell extends ListCell<OrderInfo> {

    @FXML
    private Label fromLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private HBox orderListCellHBox;

    @FXML
    private FontAwesomeIconView isNewIcon;

    FXMLLoader mLLoader;

    public OrderListCell() {
        super();
    }

    @Override
    protected void updateItem(OrderInfo order, boolean empty) {
        super.updateItem(order, empty);

        if (empty || order == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("../view/OrderListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            Format dateFormatter = new SimpleDateFormat("dd.MM.yy hh:mm");
            this.titleLabel.setText(String.valueOf(order.getTitle()));
            this.dateLabel.setText(dateFormatter.format(order.getSentDate()));
            this.fromLabel.setText(order.getFrom());
            if (order.isNew()) {
                isNewIcon.setStyle("-fx-text-fill: blue;");
            }
            setText(null);
            setGraphic(orderListCellHBox);
        }

    }


}
