package de.lofi.java.orderreceiver.client;

import org.junit.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MailClientTest {

    @Test
    public  void  testMailReceive() {
        String host = "pop.ionos.de";//change accordingly
        int port = 995;
        String username = "test@asiafood-togo.de";
        String password = "#Test01012020";//change accordingly

        String dateString ="31/12/1998";
        Date afterSentDate = null;
        try {
            afterSentDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            var messages = MailClient.receivePop3Email(host, username, password, null, null, afterSentDate);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //assertEquals(1, messages.length, "TestMailReceive");
    }
}
