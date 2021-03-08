package de.lofi.java.orderreceiver.client;

import com.sun.mail.pop3.POP3Store;
import de.lofi.java.orderreceiver.app.Constants;
import de.lofi.java.orderreceiver.data.AppData;
import de.lofi.java.orderreceiver.model.FileInfo;
import de.lofi.java.orderreceiver.model.OrderInfo;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailClient {


    public static void testConnection(String pop3Host,
                                      String user, String password) throws NoSuchProviderException, MessagingException {

        // Get the session object
        Properties properties = new Properties();
        properties.put(Constants.MAIL_POP3_HOST, pop3Host);
        Session emailSession = Session.getDefaultInstance(properties);

        // Create the POP3 store object and connect with the pop server
        POP3Store emailStore = (POP3Store) emailSession.getStore(Constants.MAIL_STORE_TYPE);
        emailStore.connect(user, password);
    }

    public static List<OrderInfo> receivePop3Email(String pop3Host,
                                                   String user, String password, String subjectFilter, String fromFilter, Date lastSentDate)
            throws NoSuchProviderException, MessagingException, IOException {

        List<OrderInfo> orderInfos = new ArrayList<>();
        // Get the session object
        Properties properties = new Properties();
        properties.put(Constants.MAIL_POP3_HOST, pop3Host);
        Session emailSession = Session.getDefaultInstance(properties);

        // Create the POP3 store object and connect with the pop server
        POP3Store emailStore = (POP3Store) emailSession.getStore(Constants.MAIL_STORE_TYPE);
        emailStore.connect(user, password);

        // Create the folder object and open it
        Folder emailFolder = emailStore.getFolder(Constants.MAIL_STORE_FOLDER_INBOX);
        emailFolder.open(Folder.READ_ONLY);

        // Retrieve the messages from the folder in an array , write to file
        Message[] messages = emailFolder.getMessages();
        int currentIndex = AppData.getInstance().getOrderInfoList().getCounter();
        for (Message message : messages) {

            InternetAddress address = (InternetAddress) message.getFrom()[0];

            //Filter message
            if(!filterMessage(message, subjectFilter, fromFilter, lastSentDate)) {
                continue;
            }

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(currentIndex);
            orderInfo.setTitle(message.getSubject());
            orderInfo.setFrom(address.getPersonal());
            orderInfo.setSentDate(message.getSentDate());
            orderInfo.setReceivedDate(new Date());
            orderInfo.setNew(true);
            setOrderFileInfos(message, orderInfo);
            orderInfos.add(orderInfo);
            currentIndex++;
        }

        //5) close the store and folder objects
        emailFolder.close(false);
        emailStore.close();
        return orderInfos;

    }

    private static boolean filterMessage(Message message, String subjectFilter, String fromFilter, Date lastSentDate) throws MessagingException {

        InternetAddress address = (InternetAddress) message.getFrom()[0];

        if ((subjectFilter != null && !message.getSubject().toLowerCase().contains(subjectFilter.toLowerCase())) ||
                (fromFilter != null && !address.getPersonal().toLowerCase().contains(fromFilter.toLowerCase()))) {
            return false;
        }

        if (lastSentDate != null &&  message.getSentDate().getTime() <= lastSentDate.getTime()) {
            System.out.println("Message Sent Date: " + message.getSentDate());
            System.out.println("After Sent Date: " + lastSentDate);
            return false;
        }

//        String fileName = (Constants.ORDER_FOLDER + File.separator + message.getSentDate().getTime() + "_" + address.getPersonal().toString().trim() + "_0" + Constants.MAIL_FILE_EXTENSION).trim();
//        File file = new File(fileName);
//
//        if  (file.exists()) {
//            return false;
//        }
        return true;
    }

    private static void setOrderFileInfos(Message message, OrderInfo orderInfo) throws MessagingException, IOException {
        List<FileInfo> fileInfos = new ArrayList<>();
        InternetAddress address = (InternetAddress) message.getFrom()[0];
        String fileName = (Constants.ORDER_FOLDER + File.separator + message.getSentDate().getTime() + "_" + address.getPersonal().toString().trim() + "_0" + Constants.MAIL_FILE_EXTENSION).trim();
        FileInfo mainFileInfo = new FileInfo();
        mainFileInfo.setTitle(message.getSubject());
        mainFileInfo.setFilePath(fileName);
        mainFileInfo.setType(FileInfo.FileInfoType.ORDER);
        fileInfos.add(mainFileInfo);
        System.out.println(mainFileInfo);

        if (message.isMimeType("multipart/*")) {
            System.out.println("Verarbeite multipart/* Nachricht");
            MimeMultipart mp = (MimeMultipart) message.getContent();
            // Main Body Content
            if (mp.getCount() > 1) {
                MimeBodyPart bodyPart = (MimeBodyPart) mp.getBodyPart(0);
                String content = getTextFromMimeMultipart(
                        (MimeMultipart) bodyPart.getContent());
                saveToFile(content, fileName);

                // Attachment
                for (int j = 1; j < mp.getCount(); j++) {
                    FileInfo fileInfo = new FileInfo();
                    if (j < FileInfo.FileInfoType.values().length) {
                        fileInfo.setType(FileInfo.FileInfoType.values()[j]);
                    } else {
                        fileInfo.setType(FileInfo.FileInfoType.BONS);
                    }
                    MimeBodyPart mimePart = (MimeBodyPart) mp.getBodyPart(j);
                    String disp = mimePart.getDisposition();
                    if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
                        fileInfo.setTitle(mimePart.getFileName());
                        String attachmentFilename = (Constants.ORDER_FOLDER + File.separator + message.getSentDate().getTime() + "_" + address.getPersonal().toString().trim() + "_" + String.valueOf(j) + Constants.MAIL_FILE_EXTENSION);
                        saveToFile(mimePart.getContent().toString(), attachmentFilename);
                        fileInfo.setTitle(mimePart.getFileName());
                        fileInfo.setFilePath(attachmentFilename);
                        fileInfos.add(fileInfo);
                        System.out.println(fileInfo);
                    }
                }

            } else {
                String content = getTextFromMimeMultipart((MimeMultipart) message.getContent());
                saveToFile(content, fileName);
            }
        } else {
            saveToFile(message.getContent().toString(), fileName);
        }
        orderInfo.setFileInfos(fileInfos);
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/html")) {
                result = (String) bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                return getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }


    private static void saveToFile(String content, String fileName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}