package com.example.geodes_____watch.Sched_section.add_alertt_recycle_view;

import java.util.HashSet;
import java.util.Set;

public class DataModel5 {
    private String alertTitle;
    private int imageResource;
    private Set<String> selectedIds;

    private String unid;

    public DataModel5(String alertTitle, int imageResource, String unid) {
        this.alertTitle = alertTitle;
        this.imageResource = imageResource;
        this.selectedIds = new HashSet<>();
        this.unid = unid;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public int getImageResource() {
        return imageResource;
    }

    public Set<String> getSelectedIds() {
        return selectedIds;
    }

    public String getUnid() {
        return unid;
    }

    public void toggleSelected(String itemId) {
        if (selectedIds.contains(itemId)) {
            selectedIds.remove(itemId);
        } else {
            selectedIds.add(itemId);
        }
    }

}
