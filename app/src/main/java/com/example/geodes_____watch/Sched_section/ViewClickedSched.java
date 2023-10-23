package com.example.geodes_____watch.Sched_section;


import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.AdapterSched;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.DataModelSched;

import java.util.ArrayList;
import java.util.List;

public class ViewClickedSched extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_schedules);

    }

}

