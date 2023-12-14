package com.example.geodes_____watch.AlertSection;

import static android.content.ContentValues.TAG;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;


import com.example.geodes_____watch.Constants;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceBroadcastReceiver;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.AlertSection.alerts_adapter.Adapter;
import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlertsActivity extends ComponentActivity implements Adapter.OnItemClickListener {

    private List<DataModel> dataList;
    private Adapter adapter;
    private FirebaseFirestore firestore;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper();


        dataList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        String userEmail = Constants.user_email;

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

        adapter = new Adapter(dataList, this, firestore, this);
        adapter.setOnItemClickListener(this); // Set the listener
        WearableRecyclerView recyclerView = findViewById(R.id.viewerAlerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(false);
    }

    private void processSnapshot(QuerySnapshot snapshot) {
        for (QueryDocumentSnapshot document : snapshot) {
            String alertName = document.getString("alertName");
            String notes = document.getString("notes");
            Boolean EntryExit = document.getBoolean("EntryType");
            Boolean alertEnabled = document.getBoolean("alertEnabled");
            String uniID = document.getString("uniqueID");
            Double outerRad = document.getDouble("outerRadius");
            Double innerRad = document.getDouble("innerRadius");
            String innerCode = document.getString("innerCode");
            String outerCode = document.getString("outerCode");
            String ExitCode = document.getString("exitCode");


            float outerRadii = outerRad != null ? outerRad.floatValue() : Float.NaN;
            float innerRadii = innerRad != null ? innerRad.floatValue() : Float.NaN;

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
            dataList.add(new DataModel(alertName, notes, R.drawable.baseline_calendar_month_24, R.drawable.baseline_location_on_24, alertEnabled, alertStat, latitude, longitude, uniID, EntryExit, outerRadii, innerRadii, innerCode, outerCode, ExitCode));
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






    public void addGeofence(GeoPoint latLng, float radius, String requestId, String geofenceName, boolean addEntryGeofence) {
        Geofence geofenceExit = geofenceHelper.createExitGeofence(latLng, radius, requestId);
        // Create GeofencingRequest for exit geofence
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder()
                .addGeofence(geofenceExit);

        if (addEntryGeofence) {
            // If true, then entry geofence would be added
            Geofence geofenceEntry = geofenceHelper.createEntryGeofence(latLng, radius, requestId);
            geofencingRequestBuilder.addGeofence(geofenceEntry);
        }

        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        PendingIntent pendingIntent = getGeofencePendingIntent(geofenceName);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }


    public void clearGeo(String inner, String outer) {
        List<String> innerList = Collections.singletonList(inner);
        List<String> outerList = Collections.singletonList(outer);

        geofencingClient.removeGeofences(innerList)
                .addOnSuccessListener(aVoid -> {
                    Log.i("GeofenceRemoval", "Inner geofence removed successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("GeofenceRemoval", "Failed to remove inner geofence: " + e.getMessage());
                });

        geofencingClient.removeGeofences(outerList)
                .addOnSuccessListener(aVoid -> {
                    Log.i("GeofenceRemoval", "Outer geofence removed successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("GeofenceRemoval", "Failed to remove outer geofence: " + e.getMessage());
                });
    }


    public void removeGeofence(String id) {
        // Get the PendingIntent associated with the geofence
        PendingIntent geofencePendingIntent = getGeofencePendingIntent(id);

        // Remove the geofence using the geofencingClient
        geofencingClient.removeGeofences(geofencePendingIntent)
                .addOnSuccessListener(aVoid -> {
                    // Geofence removal was successful
                    Log.i("GeofenceRemoval", "Geofence removed successfully: " + id);
                })
                .addOnFailureListener(e -> {
                    // Geofence removal failed
                    Log.e("GeofenceRemoval", "Failed to remove geofence " + id + ": " + e.getMessage());
                });
    }



    private PendingIntent getGeofencePendingIntent(String geofenceName) {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        int requestCode = 0;
        // Pass geofenceName as an extra to the intent
        intent.putExtra("GEOFENCE_NAME", geofenceName);
        intent.setAction("com.example.geodes_____watch.main_app.create_geofence_functions.ACTION_GEOFENCE_TRANSITION");
        return PendingIntent.getBroadcast(this, requestCode, intent, flags);}










}