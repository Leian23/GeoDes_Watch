package com.example.geodes_____watch.Sched_section.schedule_items;

public class DataModel1 {
    private String timeSched;
    private String alarmsSaved;
    private String titleSetSched;
    private boolean alertSwitch;
    private int alarmIc;
    private int calendarIc;
    private int clockIc;

    public DataModel1(String timeSched, String alarmsSaved, String titleSetSched, boolean alertSwitch, int alarmIc, int calendarIc, int clockIc) {
        this.timeSched = timeSched;
        this.alarmsSaved = alarmsSaved;
        this.titleSetSched = titleSetSched;
        this.alertSwitch = alertSwitch;
        this.alarmIc = alarmIc;
        this.calendarIc = calendarIc;
        this.clockIc = clockIc;
    }

    public String getTimeSched() {
        return timeSched;
    }

    public String getAlarmsSaved() {
        return alarmsSaved;
    }

    public String getTitleSetSched() {
        return titleSetSched;
    }

    public boolean isAlertSwitch() {
        return alertSwitch;
    }
    public int getClockIc() {
        return clockIc;
    }
    public int getAlertIc() {
        return alarmIc;
    }
    public int getCalendarIc() {
        return calendarIc;
    }

    // Add getters and setters for other fields if needed
}
