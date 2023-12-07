package com.example.geodes_____watch.AlertSection.alerts_adapter;

public class DataModel {
    private String TitleAlerts;
    private String  NotesAlerts;
    private int Imgcalendar;
    private int pinIcon;

    private String ListAlarms;
    private boolean alertSwitch;

    private int alertPref;

    private double latitude;
    private double longitude;
    private String uid;

    public DataModel(String TitleAlerts, String NotesAlerts,int Imgcalendar, int pinIcon, boolean alertSwitch, int alertPref, double latitude, double longitude, String uid) {
        this.TitleAlerts = TitleAlerts;
        this.NotesAlerts = NotesAlerts;
        this.Imgcalendar = Imgcalendar;
        this.pinIcon = pinIcon;
        this.alertSwitch = alertSwitch;
        this.alertPref = alertPref;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
    }

    public String getTitleAlerts() {
        return TitleAlerts;
    }
    public String getNotesAlerts() {
        return NotesAlerts;
    }
    public int getImgcalendar() { return Imgcalendar; }
    public int getPinIcon() { return pinIcon; }
    public int getAlertPref() {return alertPref;}
    public boolean getAlertSwitch1() { return alertSwitch; }


    public void setAlertSwitch1(boolean alertSwitch) {
        this.alertSwitch = alertSwitch;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;

    }

    public String getUid() {
        return uid;
    }
}
