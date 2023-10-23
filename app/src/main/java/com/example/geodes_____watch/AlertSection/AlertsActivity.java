package com.example.geodes_____watch.AlertSection;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;
import com.example.geodes_____watch.AlertSection.alerts_adapter.Adapter;
import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel;
import com.example.geodes_____watch.Sched_section.ScheduleActivity;
import com.example.geodes_____watch.Sched_section.ViewClickedSched;
import com.example.geodes_____watch.Sched_section.schedule_items.DataModel1;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends ComponentActivity implements Adapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        List<DataModel> dataList = new ArrayList<>();
        dataList.add(new DataModel("School", "Go to School",R.drawable.baseline_calendar_month_24, "Mon, Tue, Wed", R.drawable.baseline_location_on_24, "Alert#1, ALert2, Alter3",true,  R.drawable.entry));
        dataList.add(new DataModel("School", "Go to School",R.drawable.baseline_calendar_month_24, "Mon, Tue, Wed", R.drawable.baseline_location_on_24, "Alert#1, ALert2, Alter3", true, R.drawable.exit));
        dataList.add(new DataModel("School", "Go to School",R.drawable.baseline_calendar_month_24, "Mon, Tue, Wed", R.drawable.baseline_location_on_24, "Alert#1, ALert2, Alter3", true, R.drawable.entry));

        WearableRecyclerView recyclerViewww = findViewById(R.id.viewerAlerts);
        recyclerViewww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter adapter = new Adapter(dataList, this);
        adapter.setOnItemClickListener(this); // Set the listener
        recyclerViewww.setAdapter(adapter);
        recyclerViewww.setEdgeItemsCenteringEnabled(false);
    }


    @Override
    public void onItemClick(DataModel data) {
        Intent intent = new Intent(AlertsActivity.this, ViewAlertAct.class);
        startActivity(intent);
    }

}

