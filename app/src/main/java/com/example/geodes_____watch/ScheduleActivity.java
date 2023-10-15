package com.example.geodes_____watch;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.recentAlerts_Adapter.Adapter3;
import com.example.geodes_____watch.recentAlerts_Adapter.DataModel3;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);


        List<DataModel3> dataList = new ArrayList<>();
        dataList.add(new DataModel3("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel3("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel3("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel3("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));

        WearableRecyclerView recyclerVieww = findViewById(R.id.viewer);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter3 adapter1 = new Adapter3(dataList, this);
        recyclerVieww.setAdapter(adapter1);
        recyclerVieww.setEdgeItemsCenteringEnabled(false);


    }


}

