package de.lofi.java.orderreceiver.client;

import com.google.gson.Gson;
import de.lofi.java.orderreceiver.app.Constants;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JonUtil {

    @Test
    public void saveJson() {
        String filePath = Constants.ORDERS_INFO__FILE;
        Gson gson = new Gson();
        User user = new User(1, "Tom Smith", "American");
        try {
            FileWriter writer = new FileWriter("users.json");
            gson.toJson(user, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadJson() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("users.json"));
            User user = gson.fromJson(bufferedReader, User.class);
            System.out.println(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class User {
        private int id;
        private String name;
        private transient String nationality;

        public User(int id, String name, String nationality) {
            this.id = id;
            this.name = name;
            this.nationality = nationality;
        }

        public User(int id, String name) {
            this(id, name, null);
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", nationality='" + nationality + '\'' +
                    '}';
        }
    }
}


