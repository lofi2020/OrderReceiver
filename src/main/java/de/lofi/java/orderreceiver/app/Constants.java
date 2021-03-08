package de.lofi.java.orderreceiver.app;

import java.io.File;

public class Constants {

    // Formats
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    // Mails
    public static final String MAIL_STORE_TYPE = "pop3";
    public static final String MAIL_POP3_HOST = "mail.pop3.host";
    public static final String MAIL_STORE_FOLDER_INBOX = "inbox";
    public static final int MAIL_POP3_PORT = 995;

    // File Paths
    public static final String CONFIG_FILE = "settings.json";
    public static final String APP_FOLDER = System.getProperty("user.dir");
    public static final String ORDER_FOLDER = APP_FOLDER + File.separator + "orders";
    public static final String ORDERS_INFO__FILE = "orders.json";
    public static final String MAIL_FILE_EXTENSION = ".htm" ;

    // Messages
    public static final String MESSAGE_STATUS_MAIL_UPDATE = "Neue Bestellung(en): %s, Letzte Update: %s";
    public static final String MESSAGE_TEXT_SELECT_A_ORDER_TO_DELETE = "Bitte einen Auftrag zum Löschen auswählen!";
    public static final String MESSAGE_TITLE_DELETE_ORDER = "Auftrag-Löschen";
    public static final String MESSAGE_TITLE_NEW_ORDERS = "Neue Beestellung(en)";
    public static final String MESSAGE_TEXT_NEW_ORDERS = "Sie haben %s neue Beestellung(en).";


}
