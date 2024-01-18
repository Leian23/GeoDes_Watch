package com.example.geodes_____watch.Sched_section;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;


import com.example.geodes_____watch.AlertSection.ViewAlertAct;
import com.example.geodes_____watch.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class ViewClickedSched extends ComponentActivity {

    private TextView StartSched;
    private TextView RepeatSched;

    private TextView AlertList;

    private ImageButton deleteSched;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.view_schedules);


        AlertList = findViewById(R.id.listAlerts);
        StartSched = findViewById(R.id.SchedStartTime);
        RepeatSched = findViewById(R.id.textWeekdays);
        deleteSched = findViewById(R.id.deleteSched);

        String ListSched = getIntent().getStringExtra("ListSched");
        String timeSched = getIntent().getStringExtra("timeSched");
        String daySched = getIntent().getStringExtra("daySched");
        String SchedId = getIntent().getStringExtra("SchedId");

        AlertList.setText(ListSched);
        StartSched.setText(timeSched);
        RepeatSched.setText(daySched);

        deleteSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ImageButton", "DeleteAlert1 tapped");
                showConfirmationDialog(SchedId);
            }
        });
    }



    private void deleteSchedFromFirestore(String alertId) {



        DocumentReference SchedItem = firestore.collection("geofenceSchedule").document(alertId);

        // Use a batch write to delete documents from both collections atomically
        firestore.runBatch(batch -> {
                    batch.delete(SchedItem);
                })
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ViewClickedSched.this, "Alert deleted", Toast.LENGTH_SHORT).show();
                    // Refresh the alerts after deletion
                    finish();


                })
                .addOnFailureListener(e -> {
                    Log.e("AlertsFragment", "Error deleting alert from Firestore: " + e.getMessage());
                    Toast.makeText(ViewClickedSched.this, "Failed to delete alert", Toast.LENGTH_SHORT).show();
                });
    }

    private void showConfirmationDialog(String alertId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this Schedule?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteSchedFromFirestore(alertId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }





}



