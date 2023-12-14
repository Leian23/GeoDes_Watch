package com.example.geodes_____watch.MapSection.create_geofence_functions;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.AlertSection.AlertsActivity;
import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.MapSection.search_location.LocationResultt;
import com.example.geodes_____watch.MapSection.search_location.ResultLocation;
import com.example.geodes_____watch.MapSection.search_location.SearchResultsAdapter;
import com.example.geodes_____watch.R;

public class DismissAtivity extends ComponentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button dismissButton = findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the alarm here
                AlarmReceiver.stopAlarm();
                finish();
            }
        });



    }
}

