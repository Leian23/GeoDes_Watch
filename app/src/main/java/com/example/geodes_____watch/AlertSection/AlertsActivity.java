package com.example.geodes_____watch.AlertSection;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.AlertSection.alerts_adapter.Adapter;
import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlertsActivity extends ComponentActivity implements Adapter.OnItemClickListener {

    private List<DataModel> dataList;
    private Adapter adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        dataList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        String userEmail = "yow@gmail.com";

        // Assuming "users" is the collection name in Firestore
        CollectionReference entryCollection = firestore.collection("geofencesEntry");
        CollectionReference exitCollection = firestore.collection("geofencesExit");

        Query entryQuery = entryCollection.whereEqualTo("email", userEmail);
        Query exitQuery = exitCollection.whereEqualTo("email", userEmail);

        // Combine both queries using Tasks.whenAllSuccess
        Task<QuerySnapshot> entryTask = entryQuery.get();
        Task<QuerySnapshot> exitTask = exitQuery.get();

        Tasks.whenAllSuccess(entryTask, exitTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear(); // Clear existing data

                        // Process geofencesEntry data
                        QuerySnapshot entrySnapshot = (QuerySnapshot) task.getResult().get(0);
                        processSnapshot(entrySnapshot);

                        // Process geofencesExit data
                        QuerySnapshot exitSnapshot = (QuerySnapshot) task.getResult().get(1);
                        processSnapshot(exitSnapshot);

                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                        Exception exception = task.getException();
                        if (exception != null) {
                            // Handle the exception
                        }
                    }
                });

        adapter = new Adapter(dataList, this, firestore);
        adapter.setOnItemClickListener(this); // Set the listener
        WearableRecyclerView recyclerView = findViewById(R.id.viewerAlerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(false);
    }

    private void processSnapshot(QuerySnapshot snapshot) {
        for (QueryDocumentSnapshot document : snapshot) {
            // Extract data and create DataModel objects
            String alertName = document.getString("alertName");
            String notes = document.getString("notes");
            Boolean EntryExit = document.getBoolean("EntryType");
            Boolean alertEnabled = document.getBoolean("alertEnabled");
            String uniID = document.getString("uniqueID");


            Map<String, Object> location = (Map<String, Object>) document.get("location");

                Double latitude = (Double) location.get("latitude");
                Double longitude = (Double) location.get("longitude");



            int alertStat;

            if(EntryExit) {
                alertStat = R.drawable.entry;
            } else {
                alertStat = R.drawable.exit;
            }
            // Create a DataModel object and add it to the list
            dataList.add(new DataModel(alertName, notes, R.drawable.baseline_calendar_month_24, R.drawable.baseline_location_on_24, alertEnabled, alertStat, latitude, longitude, uniID));
        }
    }

    @Override
    public void onItemClick(DataModel data) {
        Intent intent = new Intent(AlertsActivity.this, ViewAlertAct.class);


        String name = data.getTitleAlerts();
        String notes = data.getNotesAlerts();
        double getLat = data.getLatitude();
        double getLong = data.getLongitude();
        String UserID = data.getUid();


        intent.putExtra("name1", name);
        intent.putExtra("notes1", notes);
        intent.putExtra("latitude1", getLat);
        intent.putExtra("longitude1", getLong);
        intent.putExtra("UserId", UserID);

        startActivity(intent);
    }
}