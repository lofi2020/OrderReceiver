package de.lofi.java.orderreceiver.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderInfoList {
    private int counter = 0;
    private List<OrderInfo> orderInfos = new ArrayList<>();
    private Date lastUpdate;
    private Date lastSentDate;
    private int lastUpdatedCount;

    public OrderInfoList() {

    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<OrderInfo> getOrderInfos() {
        return orderInfos;
    }

    public void setOrderInfos(List<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void incrementLastItems(int increment) {
        this.counter+= increment;
        this.lastUpdatedCount = increment;
    }

    public int getLastUpdatedCount() {
        return lastUpdatedCount;
    }

    public void setLastUpdatedCount(int lastUpdatedCount) {
        this.lastUpdatedCount = lastUpdatedCount;
    }

    public Date getLastSentDate() {
        return lastSentDate;
    }

    public void setLastSentDate(Date lastSentDate) {
        this.lastSentDate = lastSentDate;
    }
}
