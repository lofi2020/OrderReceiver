package de.lofi.java.orderreceiver.data;

import org.junit.Assert;
import org.junit.Test;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Locale;

public class TestOrderPrintService {

    @Test
    public void testLookupPrintService() {
        String printerName = "BIXOLON SRP-350plus".trim().toLowerCase();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        boolean found = false;
        for(PrintService printService: printServices) {
            System.out.println(printService.getName());
            if (printService.getName().trim().toLowerCase().equals(printerName)) {

                found = true;
                break;
            }
        }
        Assert.assertEquals("testLookupPrintService", true, found);
    }
}
