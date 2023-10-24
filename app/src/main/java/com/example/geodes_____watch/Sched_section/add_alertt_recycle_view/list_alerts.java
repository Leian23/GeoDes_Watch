package com.example.geodes_____watch.Sched_section.add_alertt_recycle_view;



import android.os.Bundle;


import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;

import java.util.ArrayList;
import java.util.List;

public class list_alerts extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alerts_activity);


        //dito yung list ng choose alerts
        List<DataModelSched> dataList = new ArrayList<>();
        dataList.add(new DataModelSched("Hello"));
        dataList.add(new DataModelSched("Alert5"));
        dataList.add(new DataModelSched("Alert4"));

        WearableRecyclerView recyclerVieww = findViewById(R.id.item_alert);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterSched adapter1 = new AdapterSched(dataList, this); // Fix the instantiation here
        recyclerVieww.setAdapter(adapter1);
        recyclerVieww.setEdgeItemsCenteringEnabled(false);



    }

}

