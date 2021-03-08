package de.lofi.java.orderreceiver.model;

public class Setting {

    //Emali paramters
    private String host;
    private int port;
    private boolean useSSL = false;
    private String username;
    private String password;
    private int timeIntervall;

    // Filter
    private String subjectFilter;
    private String senderFilter;

    // Print parameters
    private boolean autoPrint = false;
    private String defaultPrinter;
    private int bonWidth;
    private int bonPadding;

    //Alarm parameters
    private boolean showAlert;
    private boolean soundAlarm;
    private String soundFile;

    public Setting() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutoPrint() {
        return autoPrint;
    }

    public void setAutoPrint(boolean autoPrint) {
        this.autoPrint = autoPrint;
    }

    public String getDefaultPrinter() {
        return defaultPrinter;
    }

    public void setDefaultPrinter(String defaultPrinter) {
        this.defaultPrinter = defaultPrinter;
    }

    public int getBonWidth() {
        return bonWidth;
    }

    public void setBonWidth(int bonWidth) {
        this.bonWidth = bonWidth;
    }

    public boolean isSoundAlarm() {
        return soundAlarm;
    }

    public void setSoundAlarm(boolean soundAlarm) {
        this.soundAlarm = soundAlarm;
    }

    public int getTimeIntervall() {
        return timeIntervall;
    }

    public void setTimeIntervall(int timeIntervall) {
        this.timeIntervall = timeIntervall;
    }

    public String getSenderFilter() {
        return senderFilter;
    }

    public void setSenderFilter(String senderFilter) {
        this.senderFilter = senderFilter;
    }

    public int getBonPadding() {
        return bonPadding;
    }

    public void setBonPadding(int bonPadding) {
        this.bonPadding = bonPadding;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getSubjectFilter() {
        return subjectFilter;
    }

    public void setSubjectFilter(String subjectFilter) {
        this.subjectFilter = subjectFilter;
    }

    public boolean isShowAlert() {
        return showAlert;
    }

    public void setShowAlert(boolean showAlert) {
        this.showAlert = showAlert;
    }
}
