


package com.example.geodes_____watch;

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
import com.example.geodes_____watch.Sched_section.ScheduleActivity;
import com.example.geodes_____watch.Settings_section.settings_activity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends ComponentActivity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1; // Add this constant

    private WearableRecyclerView recyclerViewSearchResults;
    private Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);


        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        if (recyclerViewSearchResults == null) {
            Log.e("MainActivity", "recyclerViewSearchResults is null");
        } else {
            // Continue with other initialization
            SearchResultsAdapter adapter = new SearchResultsAdapter();
            recyclerViewSearchResults.setAdapter(adapter);
        }




        // Set an OnClickListener for voiceSearchButton
        ImageButton voiceSearchButton = findViewById(R.id.voiceSearchButton);
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate voice search
                displaySpeechRecognizer();
            }
        });

        // Set an OnClickListener for textSearchButton
        ImageButton textSearchButton = findViewById(R.id.textSearchButton);
        textSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the keyboard
                launchKeyboard();
            }
        });

        Button Scheduless = findViewById(R.id.Schedules);
        Scheduless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });


        Button alerts = findViewById(R.id.Alertss);
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlertsActivity.class);
                startActivity(intent);

            }
        });

        Button maps = findViewById(R.id.Map);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, map_activity.class);
                startActivity(intent);

            }
        });


        Button settings = findViewById(R.id.Settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, settings_activity.class);
                startActivity(intent);
            }
        });


    }

    private void launchKeyboard() {
        Intent intent = new Intent("com.google.android.wearable.action.LAUNCH_KEYBOARD");
        startActivityForResult(intent, REQUEST_CODE_KEYBOARD);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_KEYBOARD:
                    String resultText = (data != null) ? data.getStringExtra("result_text") : "";
                    new GooglePlacesTask().execute(resultText);
                    break;
                case SPEECH_REQUEST_CODE:
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        String spokenText = results.get(0);
                        new GooglePlacesTask().execute(spokenText);
                    }
                    break;
            }
        }
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice search for location");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }





    private class GooglePlacesTask extends AsyncTask<String, Void, ArrayList<LocationResultt>> {
        @Override
        protected ArrayList<LocationResultt> doInBackground(String... params) {
            ArrayList<LocationResultt> results = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            // Replace "YOUR_GOOGLE_API_KEY" with your actual Google API key
            String apiKey = "AIzaSyA-PwG-IjCROFu9xXBRizCuyz8L83V8Guc";
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                    "?query=" + params[0] +
                    "&key=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                // Check if the response contains results
                if (jsonObject.has("results")) {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject resultObject = resultsArray.getJSONObject(i);
                        JSONObject geometry = resultObject.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.optDouble("lat", 0);
                        double longitude = location.optDouble("lng", 0);
                        String name = resultObject.optString("name", "");
                        String address = resultObject.optString("formatted_address", "");

                        // Construct the display name combining name and address if needed
                        String displayName = name + ", " + address;

                        results.add(new LocationResultt(displayName, latitude, longitude));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<LocationResultt> results) {
            super.onPostExecute(results);

            if (results.size() > 0) {
                // There are results from Google Places
                Intent intent = new Intent(MainActivity.this, ResultLocation.class);
                intent.putParcelableArrayListExtra("search_results", results);
                startActivity(intent);

                // Show a toast indicating that Google Places is working
                Toast.makeText(MainActivity.this, "Google Places is working", Toast.LENGTH_SHORT).show();
            } else {
                // No results from Google Places
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

