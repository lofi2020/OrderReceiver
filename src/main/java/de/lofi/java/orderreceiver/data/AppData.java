package de.lofi.java.orderreceiver.data;

import de.lofi.java.orderreceiver.model.OrderInfoList;
import de.lofi.java.orderreceiver.model.Setting;

public class AppData {

    private Setting setting ;
    private OrderInfoList orderInfoList;

    private static AppData instance ;

    AppData() {
        orderInfoList = new OrderInfoList();
        setting = new Setting();
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public OrderInfoList getOrderInfoList() {
        return orderInfoList;
    }

    public void setOrderInfoList(OrderInfoList orderInfoList) {
        this.orderInfoList = orderInfoList;
    }

}
