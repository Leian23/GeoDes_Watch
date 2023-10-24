package com.example.geodes_____watch.MapSection;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.RelativeLayout;

import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geodes_____watch.AlertSection.addAlertActivity;

import com.example.geodes_____watch.MapSection.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_____watch.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class map_activity extends ComponentActivity {

    MapView mapView;
    MyLocationNewOverlay myLocationOverlay;
    private static final double MIN_ZOOM_LEVEL = 4.0;
    private static final double MAX_ZOOM_LEVEL = 21.0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private ImageButton backpress;
    private ImageButton addGeofence;
    private ImageButton addLandmarks;

    private ImageButton cancelAddGeofence;

    private MapFunctionHandler locationHandler;
    private ImageButton cancelButton;

    private ImageButton addGeo;
    private ToggleButton toggleButton;

    private boolean isEntryMode = true;

    private boolean isLocating = false;

    private boolean isLocationEnabled = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.watch_mapview);





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
        checkLocationPermission();





        backpress = findViewById(R.id.backMap);

        backpress.setImageResource(R.drawable.baseline_my_location_24);

        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationEnabled) {
                    // Handle locating the user's current location functionality
                    locateUser();
                    // Change the button to the "Close Map"
                } else {
                    onBackPressed(); // Handle closing the map functionality
                }
            }
        });

        mapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                // User has panned the map; change the button's functionality
                if (!isLocationEnabled) {
                    enableLocationMode();
                }
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                // User has zoomed the map; change the button's functionality
                if (!isLocationEnabled) {
                    enableLocationMode();
                }
                return true;
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

        addLandmarks = findViewById(R.id.addLandmarksButton);

        addLandmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LandmarksDialog landmarksDialog = new LandmarksDialog(map_activity.this);
                landmarksDialog.show();
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
                startActivity(intent);
            }

        });


    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            enableMyLocationOverlay();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationOverlay();
            } else {
                // Handle permission denied
                Toast.makeText(this, "Location permission denied. Cannot show your location on the map.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Initialize and enable the user's location overlay on the map
    private void enableMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Center the map on the user's location (if available)
        Location lastKnownLocation = myLocationOverlay.getLastFix();
        if (lastKnownLocation != null) {
            GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mapView.getController().animateTo(startPoint);
            mapView.getController().setZoom(15.0);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void hideElements() {
        findViewById(R.id.addGeoButton).setVisibility(View.GONE);
        findViewById(R.id.addLandmarksButton).setVisibility(View.GONE);
        findViewById(R.id.backMap).setVisibility(View.GONE);



    }

    public void showElements() {
        findViewById(R.id.addGeoButton).setVisibility(View.VISIBLE);
        findViewById(R.id.addLandmarksButton).setVisibility(View.VISIBLE);
        findViewById(R.id.backMap).setVisibility(View.VISIBLE);


        RelativeLayout addAlert = findViewById(R.id.addAlertLayout);
        RelativeLayout cancelAlert = findViewById(R.id.add_cancel_layout);

        if (addAlert.getVisibility() == View.VISIBLE) {
            addAlert.setVisibility(View.GONE);
        } else if (cancelAlert.getVisibility() == View.VISIBLE) {
            cancelAlert.setVisibility(View.GONE);
        }

    }



    private void toggleLocationMode() {
        if (isLocationEnabled) {
            isLocationEnabled = false;
            backpress.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            isLocationEnabled = true;
            backpress.setImageResource(R.drawable.baseline_my_location_24);
        }
    }

    // Function to enable location mode
    private void enableLocationMode() {
        isLocationEnabled = true;
        backpress.setImageResource(R.drawable.baseline_my_location_24);
    }


    private void locateUser() {
        if (myLocationOverlay != null) {
            Location lastKnownLocation = myLocationOverlay.getLastFix();
            if (lastKnownLocation != null) {
                GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mapView.getController().animateTo(userLocation);
                mapView.getController().setZoom(15.0);
                toggleLocationMode();
            }
        }
    }



}
