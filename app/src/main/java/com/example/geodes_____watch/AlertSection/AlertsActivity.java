package com.example.geodes_____watch.AlertSection;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.AlertSection.alerts_adapter.Adapter;
import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends ComponentActivity implements Adapter.OnItemClickListener {

    private List<DataModel> dataList;
    private Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        dataList = new ArrayList<>();
        adapter = new Adapter(dataList, this);

        WearableRecyclerView recyclerView = findViewById(R.id.viewerAlerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(false);

        firestore = FirebaseFirestore.getInstance();

        String userEmail = "yow@gmail.com";

// Assuming "users" is the collection name in Firestore
        CollectionReference usersCollection = firestore.collection("geofencesEntry");

        Query query = usersCollection.whereEqualTo("email", userEmail);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataList.clear(); // Clear existing data

                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Extract data and create DataModel objects
                    String alertName = document.getString("alertName");
                    String notes = document.getString("notes");
                    // Add other fields as needed

                    // Create a DataModel object and add it to the list
                    dataList.add(new DataModel(alertName, notes, R.drawable.baseline_calendar_month_24,
                            "Mon, Tue, Wed", R.drawable.baseline_location_on_24, "Alert#1, Alert2, Alert3", true, R.drawable.entry));
                }

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

    }

    @Override
    public void onItemClick(DataModel data) {
        Intent intent = new Intent(AlertsActivity.this, ViewAlertAct.class);
        // You can pass data to the next activity using intent.putExtra if needed
        startActivity(intent);
    }
}