package com.example.geodes_____watch.Sched_section;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;


import com.example.geodes_____watch.Constants;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.schedule_items.Adapter1;
import com.example.geodes_____watch.Sched_section.schedule_items.DataModel1;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends ComponentActivity implements Adapter1.OnItemClickListener {
    private List<DataModel1> dataList;
    private ImageButton addShedule;
    private Adapter1 adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        dataList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        CollectionReference entryCollection = db.collection("geofenceSchedule");
        Query entryQuery = entryCollection.whereEqualTo("Email", Constants.user_email);

        Task<QuerySnapshot> entryTask = entryQuery.get();

        Tasks.whenAllSuccess(entryTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onSuccess: Schedule");
                        dataList.clear();

                        QuerySnapshot entrySnapshot = (QuerySnapshot) task.getResult().get(0);
                        processSnapshot(entrySnapshot);

                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                        Exception exception = task.getException();
                        if (exception != null) {
                            // Handle the exception
                        }
                    }
                });

        adapter = new Adapter1(dataList, this, db, this);
        adapter.setOnItemClickListener(this); // Set the listener
        WearableRecyclerView recyclerView = findViewById(R.id.viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(false);




        addShedule = findViewById(R.id.addSchedulelist);
        addShedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this, addSched_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void processSnapshot(QuerySnapshot snapshot) {
        for (QueryDocumentSnapshot document : snapshot) {
            String schedTitle = document.getString("Sched");
            String clock = document.getString("Time");
            String UniqueId = document.getString("uniqueID");
            String schedAlarms = document.getString("SchedAlarms");
            Boolean isAlertSwitchOn = document.getBoolean("SchedStat");


            boolean Monday = document.getBoolean("Monday");
            boolean Tuesday = document.getBoolean("Tuesday");
            boolean Wednesday = document.getBoolean("Wednesday");
            boolean Thursday = document.getBoolean("Thursday");
            boolean Friday = document.getBoolean("Friday");
            boolean Saturday = document.getBoolean("Saturday");
            boolean Sunday = document.getBoolean("Sunday");

            StringBuilder selectedDays = new StringBuilder();

            if (Monday) {
                selectedDays.append("Mon, ");
            }
            if (Tuesday) {
                selectedDays.append("Tue, ");
            }
            if (Wednesday) {
                selectedDays.append("Wed, ");
            }
            if (Thursday) {
                selectedDays.append("Thu, ");
            }
            if (Friday) {
                selectedDays.append("Fri, ");
            }
            if (Saturday) {
                selectedDays.append("Sat, ");
            }
            if (Sunday) {
                selectedDays.append("Sun, ");
            }
            String result = selectedDays.toString().trim();

            List<String> selectedItemsIds = (List<String>) document.get("selectedItemsIds");

            if (selectedItemsIds != null && !selectedItemsIds.isEmpty()) {
                // Concatenate the elements of the list into a single string
                StringBuilder stringBuilder = new StringBuilder();
                for (String itemId : selectedItemsIds) {
                    stringBuilder.append(itemId).append("\n");
                }

                // Remove the last newline character
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                int iconCal = R.drawable.calendar_ic;
                int entryImage = R.drawable.alarm_ic;
                int iconMarker = R.drawable.clock_ic;
                dataList.add(new DataModel1(schedTitle, clock, schedAlarms, iconCal, entryImage, iconMarker, isAlertSwitchOn, UniqueId, result, selectedItemsIds));
            }
        }
    }


    @Override
    public void onItemClick(DataModel1 data) {
        Intent intent = new Intent(ScheduleActivity.this, ViewClickedSched.class);
        startActivity(intent);
    }

}

