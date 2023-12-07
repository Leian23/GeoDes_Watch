package com.example.geodes_____watch.AlertSection.alerts_adapter;

public class DataModel {
    private String TitleAlerts;
    private String  NotesAlerts;
    private int Imgcalendar;
    private int pinIcon;

    private String ListAlarms;
    private boolean alertSwitch;

    private int alertPref;

    public DataModel(String TitleAlerts, String NotesAlerts,int Imgcalendar, int pinIcon, boolean alertSwitch, int alertPref  ) {
        this.TitleAlerts = TitleAlerts;
        this.NotesAlerts = NotesAlerts;
        this.Imgcalendar = Imgcalendar;
        this.pinIcon = pinIcon;
        this.ListAlarms = ListAlarms;
        this.alertSwitch = alertSwitch;
        this.alertPref = alertPref;
    }

    public String getTitleAlerts() {
        return TitleAlerts;
    }
    public String getNotesAlerts() {
        return NotesAlerts;
    }
    public int getImgcalendar() { return Imgcalendar; }
    public int getPinIcon() { return pinIcon; }
    public String getListAlarms() { return ListAlarms; }
    public int getAlertPref() {return alertPref;}
    public boolean getAlertSwitch1() { return alertSwitch; }

    // Add setters for other fields if needed

    public void setAlertSwitch1(boolean alertSwitch) {
        this.alertSwitch = alertSwitch;
    }
}
