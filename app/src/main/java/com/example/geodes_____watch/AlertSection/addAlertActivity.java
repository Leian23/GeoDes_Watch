package com.example.geodes_____watch.AlertSection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.MapSection.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.showVoiceText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class addAlertActivity extends ComponentActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1; // Add this constant
    private CardView addANote;

    private static final String API_KEY = "ade995c254e64059a8a05234230611"; // Replace with your API key

    private boolean isEntryMode = true;
    private boolean isEntrySet = true;

    private TextView coord;
    private TextView alertTitle;

    private Context context;

    private TextView notes;

    private Handler handler = new Handler(Looper.getMainLooper());
    private GeofenceHelper geofenceHelper = new GeofenceHelper();
    private boolean alertEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alert_content);

        double latitude = getIntent().getDoubleExtra("lat", 0.0);
        double longitude = getIntent().getDoubleExtra("lonng", 0.0);

        GeoPoint geoPoint = new GeoPoint(latitude, longitude);


        String coordinatesRes = latitude +"\n"+longitude;


        alertTitle = findViewById(R.id.AlertTitle);
         coord = findViewById(R.id.Coordinates);
         notes = findViewById(R.id.NotesID);




         updateWeatherView(geoPoint);



        coord.setOnLongClickListener(new View.OnLongClickListener() {
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



        ImageButton voiceSearchButton = findViewById(R.id.voiceSearchButton);
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        ImageButton textSearchButton = findViewById(R.id.textSearchButton);
        textSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchKeyboard();
            }
        });
        ImageButton saveAlertButton = findViewById(R.id.confirmAlert);
        saveAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint retrievedGeoPoint = MapFunctionHandler.getMarkerLocation();
                float outer = (float) MapFunctionHandler.getOuterRadius();
                float inner = (float) MapFunctionHandler.getInnerRadius();
                String enteredText = alertTitle.getText().toString();
                String notesText =  notes.getText().toString();

                String outerCode = geofenceHelper.OuterVal();
                String innerCode = geofenceHelper.innerVal();
                boolean alertEnabled = true;
                String isEntryorExit = "false";
                String currentUser = "yow@gmail.com";
                saveAlertToFirestore(currentUser, enteredText, retrievedGeoPoint, outer, inner, outerCode, innerCode, notesText, alertEnabled, isEntryorExit);

                Intent intent = new Intent(addAlertActivity.this,map_activity.class);
                startActivity(intent);

                alertTitle.setText("");
                notes.setText("");
                coord.setText("");
            }
        });




    }

    private void launchKeyboard() {
        Intent intent = new Intent("com.google.android.wearable.action.LAUNCH_KEYBOARD");
        startActivityForResult(intent, REQUEST_CODE_KEYBOARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_KEYBOARD:

                    String resultText = (data != null) ? data.getStringExtra("result_text") : "";
                    // Checking process if the result is for alertTitle or notes


                    AlertDialog.Builder builder = new AlertDialog.Builder(addAlertActivity.this);
                    builder.setTitle("Save to")
                            .setPositiveButton("Alert", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Set isEntrySet based on logic
                                    isEntrySet = true; // For setting notes
                                    alertTitle.setText(resultText);
                                }
                            })
                            .setNegativeButton("Notes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Set isEntrySet based on logic
                                    isEntrySet = false; // For setting notes
                                    notes.setText(resultText);
                                }
                            })
                            .show();



                    break;
                case SPEECH_REQUEST_CODE:
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        String spokenText = results.get(0);
                        // Checking process if the result is for alertTitle or notes
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(addAlertActivity.this);
                        builder1.setTitle("Save to")
                                .setPositiveButton("Alert", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Set isEntrySet based on logic
                                        isEntrySet = true; // For setting notes
                                        alertTitle.setText(spokenText);
                                    }
                                })
                                .setNegativeButton("Notes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Set isEntrySet based on logic
                                        isEntrySet = false; // For setting notes
                                        notes.setText(spokenText);
                                    }
                                })
                                .show();
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
                            View weatherView = findViewById(R.id.WeatherView);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon);
                            ImageView iconImageView = weatherView.findViewById(R.id.weatherIconImageView);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);

                            // Load the weather icon using Picasso
                            Picasso.get().load(iconUrl).into(iconImageView);
                        });

                    } else {
                        handler.post(() -> {
                            Toast.makeText(context, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    handler.post(() -> {
                        Toast.makeText(context, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    View weatherView = findViewById(R.id.WeatherView);
                    TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
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

        // Default to considering it as daytime in case of errors
        return true;
    }

    private void saveAlertToFirestore(String currentUser, String AlertName, GeoPoint Point, float outRadius, float innRadius, String outerCode, String innerCode, String Notes, boolean alertenabled, String isEntry) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUser = "yow@gmail.com";

    String geofenceId = geofenceHelper.generateRequestId();
        Map<String, Object> alertData = new HashMap<>();
        alertData.put("uniqueID", geofenceId);
        alertData.put("alertName", AlertName);
        alertData.put("email", currentUser);
        alertData.put("notes", Notes);
        alertData.put("location",Point);
        alertData.put("outerRadius",outRadius);
        alertData.put("innerRadius",innRadius);
        alertData.put("outerCode",outerCode);
        alertData.put("innerCode",innerCode);
        alertData.put("alertEnabled", alertenabled);
        alertData.put("EntryType", isEntry);

        db.collection("geofencesEntry")
                .document(geofenceId)
                .set(alertData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data added successfully to Firestore
                        // You can add a Toast or other UI feedback here if needed
                        Log.d("Firestore", "Geofence data stored successfully: " + geofenceId);
                        Toast.makeText(addAlertActivity.this, "Geofence Successfully Saved", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("Firestore", "Error storing geofence data: " + e.getMessage());
                        Toast.makeText(addAlertActivity.this, "Failed to save Geofence", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    }






