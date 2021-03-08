package de.lofi.java.orderreceiver.data;

import com.google.gson.Gson;
import de.lofi.java.orderreceiver.app.Constants;
import de.lofi.java.orderreceiver.model.FileInfo;
import de.lofi.java.orderreceiver.model.OrderInfo;
import de.lofi.java.orderreceiver.model.OrderInfoList;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class OrderManager {

    public OrderManager() {
        loadOrderInfoList();
    }

    public static String loadFileContent(String filePath) {
        String content = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                content = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void removeOrders(List<OrderInfo> orderInfos) {
        for(var orderInfo : orderInfos) {
            removeOrder(orderInfo);
        }
    }

    private void removeOrder(OrderInfo orderInfo) {
        List<OrderInfo> orderInfos = AppData.getInstance().getOrderInfoList().getOrderInfos();
        try {
        for (var orderItem : orderInfos) {
            if (orderItem.getId() == orderInfo.getId()) {
                for (FileInfo fileInfo : orderInfo.getFileInfos()) {
                   removeFile(fileInfo.getFilePath());
                }
                orderInfos.remove(orderItem);
                saveOrderInfoList();
                break;
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeAll() {
        try {
            FileUtils.cleanDirectory(new File(Constants.ORDER_FOLDER));
            OrderInfoList orderInfoList = AppData.getInstance().getOrderInfoList();
            orderInfoList.getOrderInfos().clear();
            orderInfoList.setLastUpdatedCount(0);
            saveOrderInfoList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppData.getInstance().getOrderInfoList().getOrderInfos().clear();
        saveOrderInfoList();
    }

    public void addOrderInfos(List<OrderInfo> orderInfos) {
        OrderInfoList orderInfoList = AppData.getInstance().getOrderInfoList();
        int lastUpdatedCount = Math.min(orderInfoList.getLastUpdatedCount(), orderInfoList.getOrderInfos().size());
        Date lastSentDate = orderInfoList.getLastSentDate();
        for (int i = 0; i <  lastUpdatedCount ; i++) {
            var orderInfo = orderInfoList.getOrderInfos().get(i);
            orderInfo.setNew(false);
        }
        int counter = orderInfoList.getCounter();
        for (var orderInfo : orderInfos) {
            orderInfo.setId(counter);
            orderInfoList.getOrderInfos().add(0, orderInfo);
            if (lastSentDate == null || orderInfo.getSentDate().after(lastSentDate)) {
                lastSentDate = orderInfo.getSentDate();
            }
            System.out.println("Last Sent Date" + lastSentDate);
            counter++;
        }
        orderInfoList.setLastSentDate(lastSentDate);
        orderInfoList.setLastUpdate(new Date());
        orderInfoList.incrementLastItems(orderInfos.size());
        saveOrderInfoList();
    }

    private void saveOrderInfoList() {
        OrderInfoList orderInfoList = AppData.getInstance().getOrderInfoList();
        Gson gson = new Gson();
        String filePath = Constants.ORDERS_INFO__FILE;
        try {
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(orderInfoList, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadOrderInfoList() {
        OrderInfoList orderInfoList = AppData.getInstance().getOrderInfoList();
        try {
            if ((new File(Constants.ORDERS_INFO__FILE)).exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.ORDERS_INFO__FILE));
                Gson gson = new Gson();
                var newOrderInfoList = gson.fromJson(bufferedReader, OrderInfoList.class);
                if (newOrderInfoList  != null) {
                    AppData.getInstance().setOrderInfoList(newOrderInfoList);
                    System.out.println("Loading orders from file: " + newOrderInfoList.getOrderInfos().size());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void removeFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }


}
