package de.lofi.java.orderreceiver.service;

import de.lofi.java.orderreceiver.data.AppData;
import de.lofi.java.orderreceiver.data.OrderManager;
import de.lofi.java.orderreceiver.model.FileInfo;
import de.lofi.java.orderreceiver.model.OrderInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PageFormat;

public class OrderPrintService extends Service<String> {

    private WebView webView;

    private FileInfo fileInfo;

    public OrderPrintService(WebView webView, FileInfo fileInfo) {
        this.webView = webView;
        this.fileInfo = fileInfo;
    }

    @Override
    protected Task<String> createTask() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    //printFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "Finished Printing " + fileInfo.getTitle();
            }
        };
        return task;
    }


    private void print(final Node node) {
        Printer printer = null;
        PrinterJob job = null;
        String printerName = AppData.getInstance().getSetting().getDefaultPrinter();
        if (printerName == null) {
            printer = Printer.getDefaultPrinter();
            job = PrinterJob.createPrinterJob();
        } else {
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            for(PrintService printService: printServices) {
                if (printService.getName().trim().toLowerCase().equals(printerName.trim().toLowerCase())) {
                    //job = printService.createPrintJob()
                }
            }
        }

        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }

    private PageFormat createPageFormat() {
        java.awt.print.Paper paper = new java.awt.print.Paper();
        paper.setSize(306, 396);
        paper.setImageableArea(AppData.getInstance().getSetting().getBonPadding(), AppData.getInstance().getSetting().getBonPadding(), paper.getWidth(), paper.getHeight());
        PageFormat pageFormat = new PageFormat();
        pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
        pageFormat.setPaper(paper);
        return pageFormat;
    }
}
