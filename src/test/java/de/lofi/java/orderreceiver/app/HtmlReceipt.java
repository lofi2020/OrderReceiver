package de.lofi.java.orderreceiver.app;

import javafx.application.Application;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static javafx.application.Application.launch;

public class HtmlReceipt extends Application {

    String htmlTemplate = "<html>"
            + "<head>"
            + "<style>"
            + "body {background-color: yellow;}"
            + "#label1 {"
            + "background-color:red;"
            + "border:1px solid #000"
            + "}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<span id = 'label1'></span>"
            + "</body></html>";

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane rootpane = new AnchorPane();
        Scene scene = new Scene(rootpane);
        Button button = new Button("Print");
        button.setOnAction( e -> {
            prinntWebView();
        });
        rootpane.getChildren().add(button);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void prinntWebView() {
        WebView webView = new WebView();
        webView.setPrefHeight(400);
        webView.setPrefWidth(300);
        webView.getEngine().loadContent(getReceipt("MyName"));

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            System.out.println("Starting print webview");
            webView.getEngine().print(job);
            job.endJob();
        }

    }


    public void print(final Node node) {
        Printer printer = Printer.getDefaultPrinter();
        PrinterJob job = PrinterJob.createPrinterJob();
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        double scaleX = ((PageLayout) pageLayout).getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY;
        scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));
        if (job != null) {
            System.out.println("Starting print job");
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }

    public static String getReceipt(String labelText){
        String htmlTemplate = "<html>"
                + "<head>"
                + "<style>"
                + "body {background-color: yellow;}"
                + "#label1 {"
                + "background-color:red;"
                + "border:1px solid #000"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<span id = 'label1'></span>"
                + "</body></html>";


        Document doc = Jsoup.parse(htmlTemplate);
        Element span = doc.select("span#label1").first();
        span.text(labelText);
        return doc.html();
    }
}