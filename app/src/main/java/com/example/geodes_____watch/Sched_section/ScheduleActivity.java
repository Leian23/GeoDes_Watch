package com.example.geodes_____watch.Sched_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;


import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.schedule_items.Adapter1;
import com.example.geodes_____watch.Sched_section.schedule_items.DataModel1;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends ComponentActivity {

    private ImageButton addShedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);


        List<DataModel1> dataList = new ArrayList<>();
        dataList.add(new DataModel1("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel1("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel1("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));
        dataList.add(new DataModel1("11:59 PM", "Alert#1, Alert#2", "School", true, R.drawable.baseline_access_alarm_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_access_time_24));

        WearableRecyclerView recyclerVieww = findViewById(R.id.viewer);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter1 adapter1 = new Adapter1(dataList, this);
        recyclerVieww.setAdapter(adapter1);
        recyclerVieww.setEdgeItemsCenteringEnabled(false);


        addShedule = findViewById(R.id.addSchedulelist);

        addShedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this, addSched_activity.class);
                startActivity(intent);
            }
        });


    }





}

