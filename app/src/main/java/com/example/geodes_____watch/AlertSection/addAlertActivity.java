package com.example.geodes_____watch.AlertSection;

import static com.google.android.gms.wearable.DataMap.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceBroadcastReceiver;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.MapSection.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.showVoiceText;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static boolean alertAdded = true;

    private TextView notes;


    private Handler handler = new Handler(Looper.getMainLooper());

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private MapFunctionHandler locationhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alert_content);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper();

        double latitude = getIntent().getDoubleExtra("lat", 0.0);
        double longitude = getIntent().getDoubleExtra("lonng", 0.0);
        GeoPoint markerpoint = getIntent().getParcelableExtra("markerloc");
        double outerGeo = getIntent().getDoubleExtra("outerRad", 0.0);
        double innerGeo = getIntent().getDoubleExtra("innerRad", 0.0);
        boolean entryExit = getIntent().getBooleanExtra("entryExit", false);



        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        String coordinatesRes = latitude +"\n"+longitude;

        alertTitle = findViewById(R.id.AlertTitle);
        coord = findViewById(R.id.Coordinates);
        notes = findViewById(R.id.NotesID);

        RelativeLayout layout = findViewById(R.id.infoLayout);
        layout.setVisibility(View.GONE);
        FrameLayout layout1 = findViewById(R.id.idLoad);
        layout1.setVisibility(View.VISIBLE);
        FrameLayout layoutt = findViewById(R.id.NotAvail);


        layoutt.setVisibility(View.GONE);
        updateWeatherView(geoPoint);


        String alertName = alertTitle.toString();


        ImageButton addAlert = findViewById(R.id.confirmAlert);
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(addAlertActivity.this, map_activity.class);
                intent1.putExtra("latitudee", latitude);
                intent1.putExtra("longitudee", longitude);
                intent1.putExtra("markerlocation", (Parcelable) markerpoint);
                intent1.putExtra("outerRadius", outerGeo);
                intent1.putExtra("innerRadius", innerGeo);
                intent1.putExtra("entry_Exit", entryExit);
                intent1.putExtra("AlertName", alertName);

                Toast.makeText(addAlertActivity.this, markerpoint + " " + outerGeo + " " + " " + innerGeo + " " + entryExit, Toast.LENGTH_SHORT).show();



                if(locationhandler.geTEntryOrExit()) {
                    String outer = geofenceHelper.OuterVal();
                    String inner = geofenceHelper.innerVal();
                    addGeofence(markerpoint, (float) outerGeo, outer, alertName, true);
                    addGeofence(markerpoint, (float) innerGeo, inner, alertName, true);
                    Log.d("GeofenceValues", "Outer Type: " + outer);
                    Log.d("GeofenceValues", "Inner Type: " + inner);
                    Toast.makeText(v.getContext(), "successfully stored", Toast.LENGTH_SHORT).show();
                } else if (!locationhandler.geTEntryOrExit()) {

                    Toast.makeText(v.getContext(), "the code is exit", Toast.LENGTH_SHORT).show();

                }


                startActivity(intent1);
                finish();

            }
        });





        coord.setText(coordinatesRes);

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
                displaySpeechRecognizer();
            }
        });

    }

   /* private void launchKeyboard() {
        Intent intent = new Intent("com.google.android.wearable.action.LAUNCH_KEYBOARD");
        startActivityForResult(intent, REQUEST_CODE_KEYBOARD);
    }

    */

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
                        ZonedDateTime currentTime = ZonedDateTime.now();
                        boolean isDay = isDaytime((geoPoint.getLatitude()), geoPoint.getLongitude(), currentTime);
                        // Construct the URL for the weather icon based on the icon code
                        String iconUrl = "https://cdn.weatherapi.com/weather/64x64/" + (isDay ? "day" : "night") + "/" + iconCode + ".png";

                        // Update the WeatherView with weather information and load the icon URL
                        handler.post(() -> {
                            View weatherView = findViewById(R.id.WeatherView);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon);
                            ImageView iconImageView = weatherView.findViewById(R.id.weatherIconImageView);
                            RelativeLayout layout = weatherView.findViewById(R.id.infoLayout);
                            FrameLayout layout1 = weatherView.findViewById(R.id.idLoad);
                            FrameLayout layout2 = weatherView.findViewById(R.id.NotAvail);
                            layout1.setVisibility(View.VISIBLE);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            Picasso.get().load(iconUrl).into(iconImageView);
                            layout.setVisibility(View.VISIBLE);
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
            }
        }).start();
    }


    public static boolean isDaytime(double latitude, double longitude, ZonedDateTime currentTime) {
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

                    // Convert sunrise and sunset times to local time
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                    ZonedDateTime sunriseTime = ZonedDateTime.parse(sunrise, formatter).withZoneSameInstant(ZoneId.systemDefault());
                    ZonedDateTime sunsetTime = ZonedDateTime.parse(sunset, formatter).withZoneSameInstant(ZoneId.systemDefault());

                    // Define a threshold for twilight (15 minutes before sunrise and after sunset)
                    ZonedDateTime twilightStart = sunriseTime.minusMinutes(15);
                    ZonedDateTime twilightEnd = sunsetTime.plusMinutes(15);

                    // Check if the current time is between twilight thresholds
                    return currentTime.isAfter(twilightStart) && currentTime.isBefore(twilightEnd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // Default to considering it as daytime in case of errors
    }


    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            boolean isAvailable = networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));

            Log.d("Internet", "Internet available: " + isAvailable);
            return isAvailable;
        } else {
            android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isAvailable = networkInfo != null && networkInfo.isConnected();

            Log.d("Internet", "Internet available: " + isAvailable);
            return isAvailable;
        }
    }









/*
    private void createExitGeofence(GeoPoint markerPoint, String GeoName, float outerRadius, String ExitCode) {
        outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerRadius));
        outerGeofence.setFillColor(Color.argb(102, 241, 217, 154));
        outerGeofence.setStrokeColor(Color.argb(255, 180, 158, 80));
        outerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(outerGeofence);

        Marker marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);

        // Load a new custom Bitmap or image resource for the marker
        Bitmap customBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_loc_exit);

        // Create a Drawable from the custom Bitmap
        Drawable customDrawable = new BitmapDrawable(getResources(), customBitmap);

        // Set the custom Drawable as the icon for the marker
        marker.setIcon(customDrawable);

        mapView.getOverlays().add(marker);
        mapView.invalidate();

        addGeofence(markerPoint, outerRadius, ExitCode, GeoName,false);
        Log.d("GeofenceValues", "Inner Type: " + ExitCode);
    }

 */


    private void addGeofence(GeoPoint latLng, float radius, String requestId, String geofenceName, boolean addEntryGeofence) {
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


    private void clearGeo(String inner, String outer) {
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


    private void removeGeofence(String geofenceName) {
        // Get the PendingIntent associated with the geofence
        PendingIntent geofencePendingIntent = getGeofencePendingIntent(geofenceName);

        // Remove the geofence using the geofencingClient
        geofencingClient.removeGeofences(geofencePendingIntent)
                .addOnSuccessListener(aVoid -> {
                    // Geofence removal was successful
                    Log.i("GeofenceRemoval", "Geofence removed successfully: " + geofenceName);
                })
                .addOnFailureListener(e -> {
                    // Geofence removal failed
                    Log.e("GeofenceRemoval", "Failed to remove geofence " + geofenceName + ": " + e.getMessage());
                });
    }



    private PendingIntent getGeofencePendingIntent(String geofenceName) {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        int requestCode = 0;
        // Pass geofenceName as an extra to the intent
        intent.putExtra("GEOFENCE_NAME", geofenceName);
        return PendingIntent.getBroadcast(this, requestCode, intent, flags);}






    public static boolean getAdded() {
        return alertAdded;
    }
    }