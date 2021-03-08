package de.lofi.java.orderreceiver.data;

import com.google.gson.Gson;
import de.lofi.java.orderreceiver.app.Constants;
import de.lofi.java.orderreceiver.model.Setting;

import java.io.*;

public class SettingManager {

    public SettingManager() {
        loadSettings();
    }

    /**
     *  Load settings from json file
     */
    public void loadSettings() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.CONFIG_FILE));
            Gson gson = new Gson();
            Setting setting = gson.fromJson(bufferedReader, Setting.class);
            if (setting != null) {
                AppData.getInstance().setSetting(setting);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write settings to json file
     */
    public void saveSettings() {
        try {
            FileWriter writer = new FileWriter(Constants.CONFIG_FILE);
            Gson gson = new Gson();
            gson.toJson(AppData.getInstance().getSetting(), writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
