package com.example.geodes_____watch.AlertSection;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.geodes_____watch.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewAlertAct extends ComponentActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private String userId;

    private FirebaseFirestore firestore;
    private static final String API_KEY = "ade995c254e64059a8a05234230611";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alerts);

        firestore = FirebaseFirestore.getInstance();

        String name = getIntent().getStringExtra("name1");
        String notes = getIntent().getStringExtra("notes1");
        double latitude = getIntent().getDoubleExtra("latitude1", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude1", 0.0);
        userId = getIntent().getStringExtra("UserId");


        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        String coordinatesRes = latitude + "\n" + longitude;

        TextView alertTitle = findViewById(R.id.TitleAlertName);
        TextView CoordinatesDesc = findViewById(R.id.Coordinatess);
        TextView Notes = findViewById(R.id.NotesInfo);

        CoordinatesDesc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String coordinates = "Coordinates: " + coordinatesRes;
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                Toast.makeText(v.getContext(), "Coordinates copied to clipboard", Toast.LENGTH_SHORT).show();
                ClipData clip = ClipData.newPlainText("Coordinates", coordinates.substring("Coordinates: ".length()));
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        alertTitle.setText(name);
        CoordinatesDesc.setText(coordinatesRes);
        Notes.setText(notes);
        updateWeatherView(geoPoint);
        ImageButton deleteAnAlert = findViewById(R.id.deleteAlert);

        deleteAnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ImageButton", "DeleteAlert1 tapped");
                showConfirmationDialog(userId);
            }
        });
    }


    private void deleteAlertFromFirestore(String alertId) {



        DocumentReference entryAlertDocRef = firestore.collection("geofencesEntry").document(alertId);
        DocumentReference exitAlertDocRef = firestore.collection("geofencesExit").document(alertId);

        // Use a batch write to delete documents from both collections atomically
        firestore.runBatch(batch -> {
                    batch.delete(entryAlertDocRef);
                    batch.delete(exitAlertDocRef);
                })
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ViewAlertAct.this, "Alert deleted", Toast.LENGTH_SHORT).show();
                    // Refresh the alerts after deletion
                    finish();


                })
                .addOnFailureListener(e -> {
                    Log.e("AlertsFragment", "Error deleting alert from Firestore: " + e.getMessage());
                    Toast.makeText(ViewAlertAct.this, "Failed to delete alert", Toast.LENGTH_SHORT).show();
                });
    }







    private void showConfirmationDialog(String alertId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this alert?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAlertFromFirestore(alertId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }









    private void updateWeatherView(GeoPoint geoPoint) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + geoPoint.getLatitude() + "," + geoPoint.getLongitude();
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject weatherData = new JSONObject(responseData);

                    if (weatherData.has("current")) {
                        JSONObject currentData = weatherData.getJSONObject("current");
                        double temperatureCelsius = currentData.getDouble("temp_c");

                        JSONObject conditionObject = currentData.getJSONObject("condition");
                        String conditionText = conditionObject.getString("text");

                        int iconCode = 0;

                        OkHttpClient conditionsClient = new OkHttpClient();
                        String apiUrl1 = "https://www.weatherapi.com/docs/weather_conditions.json";
                        Request request1 = new Request.Builder()
                                .url(apiUrl1)
                                .build();

                        Response response1 = conditionsClient.newCall(request1).execute();

                        if (response1.isSuccessful()) {
                            String responseData1 = response1.body().string();
                            JSONArray weatherData1 = new JSONArray(responseData1);

                            for (int i = 0; i < weatherData1.length(); i++) {
                                JSONObject condition = weatherData1.getJSONObject(i);
                                String daytime = condition.getString("day");
                                String nighttime = condition.getString("night");

                                if (conditionText.equals(daytime) || conditionText.equals(nighttime)) {
                                    iconCode = condition.getInt("icon");
                                    break; // Exit the loop once a match is found
                                }
                            }
                        }

                        // Determine if it's day or night
                        boolean isDay = isDaytime((geoPoint.getLatitude()), geoPoint.getLongitude());

                        // Construct the URL for the weather icon based on the icon code
                        String iconUrl = "https://cdn.weatherapi.com/weather/64x64/" + (isDay ? "day" : "night") + "/" + iconCode + ".png";

                        // Update the WeatherView with weather information and load the icon URL
                        handler.post(() -> {
                            View weatherView = findViewById(R.id.WeatherView2);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp2);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon2);
                            ImageView iconImageView = weatherView.findViewById(R.id.weatherIconImageView2);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);

                            // Load the weather icon using Picasso
                            Picasso.get().load(iconUrl).into(iconImageView);
                        });

                    } else {
                        handler.post(() -> {
                            Toast.makeText(ViewAlertAct.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    handler.post(() -> {
                        Toast.makeText(ViewAlertAct.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    View weatherView = findViewById(R.id.WeatherView2);
                    TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp2);
                    temperatureTextView.setText(e.getMessage());
                });
            }
        }).start();
    }

    private static boolean isDaytime(double latitude, double longitude) {
        try {
            OkHttpClient client = new OkHttpClient();
            String apiUrl = "https://api.sunrise-sunset.org/json?lat=" + latitude + "&lng=" + longitude;
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONObject sunriseSunsetData = new JSONObject(responseData);

                if (sunriseSunsetData.has("results")) {
                    JSONObject results = sunriseSunsetData.getJSONObject("results");
                    String sunrise = results.getString("sunrise");
                    String sunset = results.getString("sunset");

                    // Get the current time
                    int currentHour = java.time.LocalTime.now().getHour();
                    int currentMinute = java.time.LocalTime.now().getMinute();

                    // Parse sunrise and sunset times
                    int sunriseHour = Integer.parseInt(sunrise.split(":")[0]);
                    int sunriseMinute = Integer.parseInt(sunrise.split(":")[1]);
                    int sunsetHour = Integer.parseInt(sunset.split(":")[0]);
                    int sunsetMinute = Integer.parseInt(sunset.split(":")[1]);

                    // Check if the current time is between sunrise and sunset
                    return (currentHour > sunriseHour || (currentHour == sunriseHour && currentMinute >= sunriseMinute))
                            && (currentHour < sunsetHour || (currentHour == sunsetHour && currentMinute < sunsetMinute));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }











}
