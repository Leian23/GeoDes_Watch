package com.example.geodes_____watch.MapSection.create_geofence_functions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.app.ComponentActivity;



import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.R;



import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFunctionHandler {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

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
    private static boolean isEntryMode = true;
    private static final String API_KEY = "ade995c254e64059a8a05234230611";

    private double outerRadius = 50;
    private double innerRadius = 20;

    private double lat;
    private double lonng;

    private int initialMaxLevelOuter = 50; // maximum level of outerseekbar
    private int initialMaxLevelInner = 10; // maximum level of innerseekbar

    private static double initialSavedOuterRadius;
    private static double initialSavedInnerRadius;

    private static GeoPoint markerLocation;

    private static boolean isAlertEnabled = false;









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
        UpdateGeofence();
    }


    private void UpdateGeofence() {

        ToggleButton toggleButton = ((map_activity) context).findViewById(R.id.toggles);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEntryMode = isChecked;

            if (geofenceSetup != null) {
                geofenceSetup.updateOuterGeofenceColor(mapView.getContext(), !isChecked);

                if (!isChecked) {
                    updateGeofences(outerRadius, 0);
                } else {
                    updateGeofences(outerRadius, innerRadius);
                }

            }
        });
    }


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


    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    public void dropPinOnMap(GeoPoint geoPoint) {
        // Clear existing marker and geofences

        // Add a new marker at the long-pressed location
        mapMarker = new Marker(mapView);
        mapMarker.setPosition(geoPoint);
        mapMarker.setVisible(false);
        mapView.getOverlays().add(mapMarker);

        if (geofenceSetup == null) {
            geofenceSetup = new GeofenceSetup(context, mapView);
        }

        double outerRadius = 2; // default outer radius seekbar progress
        double innerRadius = 5; // default inner radius seekbar progress

        initialSavedOuterRadius = (outerRadius / (double) initialMaxLevelOuter) * 8000.0;
        initialSavedInnerRadius = isEntryMode ? (innerRadius / (double) 10) * 300.0 : 0; // Set your desired default inner radius


        geofenceSetup.addMarkerWithGeofences(mapView.getContext(), geoPoint.getLatitude(), geoPoint.getLongitude(),  initialSavedOuterRadius, initialSavedInnerRadius);

        // Calculate bounding box
        BoundingBox boundingBox = calculateBoundingBox(geoPoint, initialSavedInnerRadius);

        // Animate the map to the bounding box
        mapView.zoomToBoundingBox(boundingBox, true);

        // Reset map orientation
        mapView.setMapOrientation(0);

        mapView.invalidate();

        lat = geoPoint.getLatitude();
        lonng = geoPoint.getLongitude();
        getpoint(geoPoint);
        ((map_activity) context).hideElements();
        RelativeLayout showAddLayout = ((map_activity) context).findViewById(R.id.add_cancel_layout);
        showAddLayout.setVisibility(View.VISIBLE);

    }



    public void dropPinOnMap1() {
        if (geofenceSetup == null) {
            geofenceSetup = new GeofenceSetup(context, mapView);
        }
        geofenceSetup.clearGeofencesAndMarker();
        mapView.invalidate();
    }



    private BoundingBox calculateBoundingBox(GeoPoint center, double radius) {
        double halfDistanceInMeters = radius * 3.5; // Adjust as needed for better visibility
        double latPerMeter = 1.0 / 111319.9; // Approximate value for latitude degrees per meter

        double deltaLat = halfDistanceInMeters * latPerMeter * - 0.5; // Adjust to move the pin further to the top
        double deltaLon = halfDistanceInMeters / (111319.9 * Math.cos(Math.toRadians(center.getLatitude())));

        // Set latitude to the maximum latitude for the pin to be further at the top
        double pinLatitude = center.getLatitude() + deltaLat;

        double minLon = center.getLongitude() - deltaLon ; // Adjust to center the bounding box
        double maxLon = center.getLongitude() + deltaLon ; // Adjust to center the bounding box

        return new BoundingBox(pinLatitude, maxLon, center.getLatitude(), minLon);
    }

    public GeoPoint getpoint(GeoPoint markerloc) {
        markerLocation = markerloc;
        return markerloc;
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
            BoundingBox boundingBox = calculateBoundingBox(markerPosition, initialSavedOuterRadius);

            // Animate the map to the new bounding box
            mapView.zoomToBoundingBox(boundingBox, true);

            mapView.invalidate();
        }
    }

    public void setLongPressEnabled(boolean enabled) {
        isLongPressEnabled = enabled;
    }

    public double getLat() {
        return lat;
    }
    public double getLong() {return lonng;}
    public static GeoPoint getMarkerLocation() {return markerLocation;}

    public static double getOuterRadius() {return initialSavedOuterRadius; }

    public static double getInnerRadius() {
        return initialSavedInnerRadius;
    }

    public static boolean geTEntryOrExit() {return  isEntryMode; }

    public static boolean getIsAlertEnabled() {return isAlertEnabled;}

    public void setLat(double lat) {
        this.lat = lat;
    }


    public void setLong(double lonng) {
        this.lonng = lonng;
    }


    public static void setOuterRadius(double initialSavedOuterRadius) {
        MapFunctionHandler.initialSavedOuterRadius = initialSavedOuterRadius;
    }


    public static void setInnerRadius(double initialSavedInnerRadius) {
        MapFunctionHandler.initialSavedInnerRadius = initialSavedInnerRadius;
    }

    public static void setEntryOrExit(boolean isEntryMode) {
        MapFunctionHandler.isEntryMode = isEntryMode;
    }

    public static void setMarkerLocation(GeoPoint markerLocation) {
        MapFunctionHandler.markerLocation = markerLocation;
    }
}