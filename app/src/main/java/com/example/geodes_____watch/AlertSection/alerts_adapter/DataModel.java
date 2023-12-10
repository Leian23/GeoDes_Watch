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

    private Boolean EntryExit;

    private Float outerRadius;
    private Float innerradius;

    private String innerCode;

    private String outerCode;

    private String ExitCode;

    public DataModel(String TitleAlerts, String NotesAlerts,int Imgcalendar, int pinIcon, boolean alertSwitch, int alertPref, double latitude, double longitude, String uid, Boolean EntryExit, Float outerRadius, Float innerradius, String outerCode, String innerCode, String ExitCode) {
        this.TitleAlerts = TitleAlerts;
        this.NotesAlerts = NotesAlerts;
        this.Imgcalendar = Imgcalendar;
        this.pinIcon = pinIcon;
        this.alertSwitch = alertSwitch;
        this.alertPref = alertPref;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
        this.EntryExit = EntryExit;
        this.outerRadius = outerRadius;
        this.innerradius = innerradius;
        this.outerCode = outerCode;
        this.innerCode = innerCode;
        this.ExitCode = ExitCode;

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

    public Boolean getEntryExit() {
        return EntryExit;
    }

    public Float getInnerradius() {
        return innerradius;
    }

    public Float getOuterRadius() {
        return outerRadius;
    }

    public String getOuterCode() {
        return outerCode;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public String getExitCode() {
        return ExitCode;
    }
}
