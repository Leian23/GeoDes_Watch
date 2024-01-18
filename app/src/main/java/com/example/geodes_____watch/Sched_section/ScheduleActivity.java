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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.Collections;
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

            List<String> concatenatedAlertNames = new ArrayList<>();
            List<Task<QuerySnapshot>> tasks = new ArrayList<>();

            for (String id : selectedItemsIds) {
                Task<QuerySnapshot> entryTask = db.collection("geofencesEntry")
                        .whereEqualTo("uniqueID", id)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String alertName = documentSnapshot.getString("alertName");
                                if (alertName != null) {
                                    Log.d(TAG, "Match found in geofencesEntry. AlertName: " + alertName);
                                    concatenatedAlertNames.add(alertName);
                                    return;
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure if needed
                        });

                tasks.add(entryTask);

                Task<QuerySnapshot> exitTask = db.collection("geofencesExit")
                        .whereIn("uniqueID", Collections.singletonList(id))
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshotsExit -> {
                            for (DocumentSnapshot documentSnapshotExit : queryDocumentSnapshotsExit) {
                                String alertNameExit = documentSnapshotExit.getString("alertName");
                                if (alertNameExit != null) {
                                    Log.d(TAG, "Match found in geofencesExit. AlertName: " + alertNameExit);
                                    concatenatedAlertNames.add(alertNameExit);
                                    return;
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure if needed
                        });

                tasks.add(exitTask);
            }

            Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener(voids -> {
                        List<String> finalConcatenatedAlertNames = new ArrayList<>();

                        // Iterate through the results of each task
                        for (Task<?> taskResult : tasks) {
                            if (taskResult.isSuccessful()) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) taskResult.getResult();
                                // Iterate through the documents in the querySnapshot
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    String alertName = documentSnapshot.getString("alertName");
                                    if (alertName != null) {
                                        Log.d(TAG, "Match found. AlertName: " + alertName);
                                        finalConcatenatedAlertNames.add(alertName);
                                    }
                                }
                            } else {
                                Exception exception = taskResult.getException();
                                // Handle failure if needed
                                if (exception != null) {
                                    Log.e(TAG, "Task failed: " + exception.getMessage());
                                }
                            }
                        }

                        // Check if concatenatedAlertNames is not empty before converting it to a string
                        String concatenatedAlertNamesString = finalConcatenatedAlertNames.isEmpty() ? "" : finalConcatenatedAlertNames.toString();

                        String concatenatedString = concatenatedAlertNames.toString();
                        concatenatedString = concatenatedString.substring(1, concatenatedString.length() - 1);


                        int iconCal = R.drawable.calendar_ic;
                        int entryImage = R.drawable.alarm_ic;
                        int iconMarker = R.drawable.clock_ic;
                        dataList.add(new DataModel1(schedTitle, clock, schedAlarms, iconCal, entryImage, iconMarker, isAlertSwitchOn, UniqueId, result, selectedItemsIds, concatenatedString));

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure if needed
                        Log.e(TAG, "Failed to retrieve alert names: " + e.getMessage());
                    });

        }
    }

    @Override
    public void onItemClick(DataModel1 data) {
        Intent intent = new Intent(ScheduleActivity.this, ViewClickedSched.class);

        String time = data.getTimeStart();
        String day = data.getSchedules();
        String ListAlerts = data.getConcatenatedAlertNames();
        String schedId = data.getUniqueId();

        intent.putExtra("timeSched", time);
        intent.putExtra("daySched", day);
        intent.putExtra("ListSched", ListAlerts);
        intent.putExtra("SchedId", schedId);

        startActivity(intent);
    }
}
