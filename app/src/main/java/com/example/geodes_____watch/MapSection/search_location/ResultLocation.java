package com.example.geodes_____watch.MapSection.search_location;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.MapSection.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.R;


import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ResultLocation extends ComponentActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_window);


        // Retrieve search results from the intent
        ArrayList<LocationResultt> searchResults = getIntent().getParcelableArrayListExtra("search_results");

        // Check if searchResults is not null and contains results
        if (searchResults != null && !searchResults.isEmpty()) {
            // Display the results in WearableRecyclerView
            WearableRecyclerView recyclerView = findViewById(R.id.recyclerViewSearchResults);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // Create and set the adapter for the WearableRecyclerView
            SearchResultsAdapter adapter = new SearchResultsAdapter();
            recyclerView.setAdapter(adapter);
            adapter.setData(searchResults);

            adapter.setOnItemClickListener(locationResult -> {
                // Handle the click action here, display coordinates as a toast
                GeoPoint point = new GeoPoint(locationResult.getLatitude(), locationResult.getLongitude());

                // Create an Intent to start the map_activity
                Intent mapIntent = new Intent(ResultLocation.this, map_activity.class);
                // Pass the selected location information as extras
                mapIntent.putExtra("latitude", locationResult.getLatitude());
                mapIntent.putExtra("longitude", locationResult.getLongitude());
                startActivity(mapIntent);

                showToast(locationResult.getLatitude() + ", " + locationResult.getLongitude());
            });


        }


    }

    private void showToast(String message) {
        // Display toast message
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
