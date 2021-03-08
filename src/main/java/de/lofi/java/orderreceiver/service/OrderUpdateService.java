package de.lofi.java.orderreceiver.service;

import de.lofi.java.orderreceiver.data.AppData;
import de.lofi.java.orderreceiver.app.Constants;
import de.lofi.java.orderreceiver.model.OrderInfo;
import de.lofi.java.orderreceiver.model.Setting;
import de.lofi.java.orderreceiver.client.MailClient;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderUpdateService extends Service<String> {

    List<OrderInfo> orderInfos = new ArrayList<>();

    Setting setting = AppData.getInstance().getSetting();

    @Override
    protected Task<String> createTask() {
        var task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                try {
                    orderInfos = MailClient.receivePop3Email(
                            setting.getHost(),
                            setting.getUsername(),
                            setting.getPassword(),
                            setting.getSubjectFilter(),
                            setting.getSenderFilter(),
                            AppData.getInstance().getOrderInfoList().getLastSentDate());
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return "Authentifizierungsfehler";
                } catch (IOException e) {
                    e.printStackTrace();
                    return  "Verbindungsfehler";
                }
                System.out.println("Retrieve orders: " + new Date() + ", new items: " + orderInfos.size());
                DateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                return String.format(Constants.MESSAGE_STATUS_MAIL_UPDATE, orderInfos.size(), formatter.format(new Date()));
            }
        };
        return task;
    }

    public List<OrderInfo> getOrderInfos() {
        return orderInfos;
    }

    public void setOrderInfos(List<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }
}
