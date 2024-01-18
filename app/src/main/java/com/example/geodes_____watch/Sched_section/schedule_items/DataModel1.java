package com.example.geodes_____watch.Sched_section.schedule_items;

import java.util.List;

public class DataModel1 {
    private String schedTitle;
    private String schedAlarms;
    private int iconCal;
    private int entryImage;
    private int iconMarker;
    private Boolean isAlertSwitchOn;

    private String getTime;

    private String uniqueId;

    private String Schedules;

    private List<String> selectedItemsIds;

    private String concatenatedAlertNames; // New field for concatenated alert names

    public DataModel1(String schedTitle, String getTime, String schedAlarms, int iconCal, int entryImage, int iconMarker, Boolean isAlertSwitchOn, String uniqueId, String Schedules, List<String> selectedItemsIds, String concatenatedAlertNames) {
        this.schedTitle = schedTitle;
        this.getTime = getTime;
        this.schedAlarms = schedAlarms;
        this.isAlertSwitchOn = isAlertSwitchOn;
        this.uniqueId = uniqueId;
        this.Schedules = Schedules;
        this.selectedItemsIds = selectedItemsIds;
        this.iconCal = iconCal;
        this.entryImage = entryImage;
        this.iconMarker = iconMarker;
        this.concatenatedAlertNames = concatenatedAlertNames;
    }

    public String getSchedTitle() {
        return schedTitle;
    }

    public String getTimeStart() {
        return getTime;
    }

    public String getSchedAlarms() {
        return schedAlarms;
    }

    public int getIconCal() {
        return iconCal;
    }

    public int getEntryImage() {
        return entryImage;
    }

    public int getIconMarker() {
        return iconMarker;
    }

    public boolean isAlertSwitchOn() {
        return isAlertSwitchOn;
    }

    public void setAlertSwitchOn(boolean alertSwitchOn) {
        this.isAlertSwitchOn = alertSwitchOn;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSchedules() {
        return Schedules;
    }

    public List<String> getSelectedItemsIds() {
        return selectedItemsIds;
    }

    public String getConcatenatedAlertNames() {
        return concatenatedAlertNames;
    }

    // Add getters and setters for other fields if needed
}
