package com.example.geodes_____watch.MapSection.create_geofence_functions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ComponentActivity;


import com.example.geodes_____watch.MainActivity;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class MapFunctionHandler {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final long MIN_TIME_BETWEEN_UPDATES = 0; // 500 milliseconds
    private static final float MIN_DISTANCE_BETWEEN_UPDATES = 10;

    private Context context;
    private MapView mapView;
    private IMapController mapController;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isGpsProviderEnabled = true;
    private boolean isLocationUpdatesInitialized = false;
    private Marker mapMarker;

    private GeofenceSetup geofenceSetup;

    private boolean isLongPressEnabled = true;
    private boolean isEntryMode = true;




    public MapFunctionHandler(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;


        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                if (isLongPressEnabled) {
                    dropPinOnMap(geoPoint);
                    return true;
                } else {
                    return false; // Long press is disabled
                }
            }
        });
        this.mapView.getOverlays().add(0, mapEventsOverlay);

        // Check and request location permissions at runtime
        if (context instanceof ComponentActivity) {
            ComponentActivity activity = (ComponentActivity) context;

            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            // For devices running versions below Marshmallow, no need to check permissions, just initialize location updates
            initializeLocationUpdates();
        }

    }



 /*

        // Inside setupSeekBarListeners method
        ToggleButton toggleButton = ((map_activity) context).findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEntryMode = isChecked;
            updateInnerSeekBarState();

            // Call the method to update the color of the outer geofence based on the toggle button state
            if (geofenceSetup != null) {
                geofenceSetup.updateOuterGeofenceColor(!isChecked);
            }
        });


  */



    private void initializeLocationUpdates() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                // Consider whether you really need to stop updates here
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (!isGpsProviderEnabled) {
                    isGpsProviderEnabled = true;
                    requestLocationUpdates(); // Request new location updates when GPS is enabled again
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                showToast("GPS provider is disabled. Please enable it.");
                isGpsProviderEnabled = false;
            }
        };

        // Request location updates
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (context instanceof ComponentActivity) {
            ComponentActivity activity = (ComponentActivity) context;

            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            // Handle the case where the context is not a ComponentActivity
        }

    }

    private void updateLocationOnMap(GeoPoint geoPoint) {
        mapController.setCenter(geoPoint);
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void dropPinOnMap(GeoPoint geoPoint) {
        // Clear existing marker and geofences
        clearMarkerAndGeofences();

        // Add a new marker at the long-pressed location
        mapMarker = new Marker(mapView);
        mapMarker.setPosition(geoPoint);
        mapView.getOverlays().add(mapMarker);

        if (geofenceSetup == null) {
            geofenceSetup = new GeofenceSetup(context, mapView);
        }

        double outerRadius = 50; // Set your desired default outer radius
        double innerRadius = isEntryMode ? 20 : 0; // Set your desired default inner radius



        geofenceSetup.addMarkerWithGeofences(geoPoint.getLatitude(), geoPoint.getLongitude(), outerRadius, innerRadius);

        // Calculate bounding box
        BoundingBox boundingBox = calculateBoundingBox(geoPoint, outerRadius);

        // Animate the map to the bounding box
        mapView.zoomToBoundingBox(boundingBox, true);

        // Reset map orientation
        mapView.setMapOrientation(0);

        mapView.invalidate();


        /*

        // Set the toggle button to true
        ToggleButton toggleButton = ((map_activity) context).findViewById(R.id.toggleButton);
        toggleButton.setChecked(true);
        */

    }


    private BoundingBox calculateBoundingBox(GeoPoint center, double radius) {
        double halfDistanceInMeters = radius * 1.5; // Adjust as needed for better visibility
        double latPerMeter = 1.0 / 111319.9; // Approximate value for latitude degrees per meter

        double deltaLat = halfDistanceInMeters * latPerMeter * - 0.5; // Adjust to move the pin further to the top
        double deltaLon = halfDistanceInMeters / (111319.9 * Math.cos(Math.toRadians(center.getLatitude())));

        // Set latitude to the maximum latitude for the pin to be further at the top
        double pinLatitude = center.getLatitude() + deltaLat;

        double minLon = center.getLongitude() - deltaLon ; // Adjust to center the bounding box
        double maxLon = center.getLongitude() + deltaLon ; // Adjust to center the bounding box

        return new BoundingBox(pinLatitude, maxLon, center.getLatitude(), minLon);
    }






    public void clearMarkerAndGeofences() {
        // Remove existing marker and geofences
        mapView.getOverlays().remove(mapMarker);
        mapView.getOverlayManager().removeIf(overlay ->
                overlay instanceof Polygon || overlay instanceof Marker);

        mapView.invalidate();
    }


    private void updateGeofences(double outerRadius, double innerRadius) {
        if (geofenceSetup != null && mapMarker != null) {
            GeoPoint markerPosition = mapMarker.getPosition();

            // Assuming the geofences are stored in MapManager, update them directly
            geofenceSetup.updateGeofences(markerPosition, outerRadius, innerRadius);

            // Calculate bounding box
            BoundingBox boundingBox = calculateBoundingBox(markerPosition, outerRadius);

            // Animate the map to the new bounding box
                mapView.zoomToBoundingBox(boundingBox, true);

            mapView.invalidate();
        }
    }


    // Inside MapFunctionHandler class

    public void setLongPressEnabled(boolean enabled) {
        isLongPressEnabled = enabled;
    }
}
