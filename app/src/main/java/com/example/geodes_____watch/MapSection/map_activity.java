package com.example.geodes_____watch.MapSection;

import static com.google.android.gms.wearable.DataMap.TAG;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.RelativeLayout;

import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;


import com.example.geodes_____watch.AlertSection.addAlertActivity;

import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceBroadcastReceiver;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.MapSection.create_geofence_functions.MapFunctionHandler;

import com.example.geodes_____watch.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.List;


public class map_activity extends ComponentActivity {

    MapView mapView;
    MyLocationNewOverlay myLocationOverlay;
    private static final double MIN_ZOOM_LEVEL = 4.0;
    private static final double MAX_ZOOM_LEVEL = 21.0;


    private ImageButton backpress;
    private ImageButton addGeofence;
    private ImageButton userlocate;

    private Context context;

    private ImageButton cancelAddGeofence;

    private MapFunctionHandler locationHandler;
    private ImageButton cancelButton;

    private ImageButton addGeo;
    private ToggleButton toggleButton;

    private boolean isEntryMode = true;

    private boolean isLocating = false;

    private boolean isLocationEnabled = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private ImageButton addGeofenceButt;
    private Polygon outerGeofence;
    private Polygon innerGeofence;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private addAlertActivity addgeoAct;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.watch_mapview);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.ViewMap);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        locationHandler = new MapFunctionHandler(map_activity.this, mapView);

        Log.d("MapViewDebug", "Tile source set to MAPNIK");
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);


        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        mapView.getController().setCenter(new GeoPoint(13.41, 122.56));
        mapView.getController().setZoom(8.0);
        mapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request ACCESS_FINE_LOCATION permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "In order for the app to work properly please enable 'Allow all the time'", Toast.LENGTH_SHORT).show();
                // If not granted, request ACCESS_BACKGROUND_LOCATION permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 456);
            }



        // Set up location request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set up location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();


                }
            }
        };
    }



        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // Check location settings
        checkLocationSettings();


        double selectedLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        double selectedLongitude = getIntent().getDoubleExtra("longitude", 0.0);


        double latitude = getIntent().getDoubleExtra("latitudee", 0.0);
        double longitude = getIntent().getDoubleExtra("longitudee", 0.0);
        GeoPoint markerpoint = getIntent().getParcelableExtra("markerlocation");
        double outerGeo = getIntent().getDoubleExtra("outerRadius", 0.0);
        double innerGeo = getIntent().getDoubleExtra("innerRadius", 0.0);
        boolean entryExit = getIntent().getBooleanExtra("entry_Exit", false);
        String alername = getIntent().getStringExtra("AlertName");






        // Check if the location information is valid
        if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
            // Create a GeoPoint from the selected location
            GeoPoint selectedLocation = new GeoPoint(selectedLatitude, selectedLongitude);

            // Drop a pin on the selected location
            locationHandler.dropPinOnMap(selectedLocation);
        }


        addGeofenceButt = findViewById(R.id.addGeofenceButton);
        addGeofenceButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint centerPoint = (GeoPoint) mapView.getMapCenter();
                locationHandler.dropPinOnMap(centerPoint);

                RelativeLayout addAlert = findViewById(R.id.addAlertLayout);
                addAlert.setVisibility(View.GONE);

            }
        });




        backpress = findViewById(R.id.backMap);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    onBackPressed();
            }
        });





        addGeofence = findViewById(R.id.addGeoButton);
        addGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideElements();

                RelativeLayout addAlert = findViewById(R.id.addAlertLayout);
                addAlert.setVisibility(View.VISIBLE);
            }
        });

        cancelAddGeofence = findViewById(R.id.cancelButton);

        cancelAddGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showElements();
            }
        });

        userlocate = findViewById(R.id.userlocator);
        userlocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locateUser();
            }

        });

        cancelButton = findViewById(R.id.cancel_add_geofence);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showElements();

                toggleButton = findViewById(R.id.toggles);
                toggleButton.setChecked(isEntryMode);
                locationHandler.clearMarkerAndGeofences();
            }

        });


        addGeo = findViewById(R.id.addButt);
        addGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(map_activity.this, addAlertActivity.class);

                double latitude = locationHandler.getLat();
                double longitude = locationHandler.getLong();
                GeoPoint markerpoint = locationHandler.getMarkerLocation();
                double outerGeo = locationHandler.getOuterRadius();
                double innerGeo = locationHandler.getInnerRadius();
                boolean entryandExit = locationHandler.geTEntryOrExit();


                intent.putExtra("lat", latitude);
                intent.putExtra("lonng", longitude);
                intent.putExtra("markerloc", (Parcelable) markerpoint);
                intent.putExtra("outerRad", outerGeo);
                intent.putExtra("innerRad", innerGeo);
                intent.putExtra("entryExit", entryandExit);

                Log.d("MapViewDebug", markerpoint + " " + outerGeo + " " + " " + innerGeo + " " + entryandExit);
                Toast.makeText(map_activity.this, markerpoint + " " + outerGeo + " " + " " + innerGeo + " " + entryandExit , Toast.LENGTH_SHORT).show();
                locationHandler.dropPinOnMap1();


                startActivity(intent);

                finish();


            }
        });



    }


    public void hideElements() {
        findViewById(R.id.addGeoButton).setVisibility(View.GONE);
        findViewById(R.id.userlocator).setVisibility(View.GONE);
        findViewById(R.id.backMap).setVisibility(View.GONE);
    }

    public void showElements() {
        findViewById(R.id.addGeoButton).setVisibility(View.VISIBLE);
        findViewById(R.id.userlocator).setVisibility(View.VISIBLE);
        findViewById(R.id.backMap).setVisibility(View.VISIBLE);


        RelativeLayout addAlert = findViewById(R.id.addAlertLayout);
        RelativeLayout cancelAlert = findViewById(R.id.add_cancel_layout);

        if (addAlert.getVisibility() == View.VISIBLE) {
            addAlert.setVisibility(View.GONE);
        } else if (cancelAlert.getVisibility() == View.VISIBLE) {
            cancelAlert.setVisibility(View.GONE);
        }

    }

    private void locateUser() {
        MyLocationNewOverlay myLocationOverlay = (MyLocationNewOverlay) mapView.getOverlays().stream()
                .filter(overlay -> overlay instanceof MyLocationNewOverlay)
                .findFirst()
                .orElse(null);

        if (myLocationOverlay != null) {
            Location lastKnownLocation = myLocationOverlay.getLastFix();
            if (lastKnownLocation != null) {
                GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mapView.getController().animateTo(userLocation);
            }
        }
    }




    private void checkLocationSettings() {
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
                requestLocationUpdates();
            } catch (ApiException exception) {
                if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        resolvable.startResolutionForResult(map_activity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException | ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            setupMyLocationOverlay(); // Call this method after obtaining location permissions
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    private void setupMyLocationOverlay() {
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location settings not satisfied, cannot proceed", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        setupMyLocationOverlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


}




