package com.example.geodes_____watch.AlertSection.alerts_adapter;

public class DataModel {
    private String TitleAlerts;
    private String  NotesAlerts;
    private int Imgcalendar;
    private String repeatDescription;
    private int pinIcon;

    private String ListAlarms;
    private boolean alertSwitch;

    public DataModel(String TitleAlerts, String NotesAlerts,int Imgcalendar, String repeatDescription, int pinIcon, String ListAlarms, boolean alertSwitch  ) {
        this.TitleAlerts = TitleAlerts;
        this.NotesAlerts = NotesAlerts;
        this.Imgcalendar = Imgcalendar;
        this.repeatDescription = repeatDescription;
        this.pinIcon = pinIcon;
        this.ListAlarms = ListAlarms;
        this.alertSwitch = alertSwitch;
    }

    public String getTitleAlerts() {
        return TitleAlerts;
    }
    public String getNotesAlerts() {
        return NotesAlerts;
    }
    public int getImgcalendar() { return Imgcalendar; }
    public String getRepeatDescription () { return repeatDescription; }
    public int getPinIcon() { return pinIcon; }
    public String getListAlarms() { return ListAlarms; }
    public boolean getAlertSwitch1() { return alertSwitch; }










    // Add getters and setters for other fields if needed
}
