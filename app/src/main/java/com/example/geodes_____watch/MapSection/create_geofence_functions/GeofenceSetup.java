package com.example.geodes_____watch.MapSection.create_geofence_functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.geodes_____watch.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class GeofenceSetup {
    private MapView mapView;
    private Polygon outerGeofence;
    private Polygon innerGeofence;
    private Marker marker;


    public GeofenceSetup(Context context, MapView mapView) {
        Configuration.getInstance().setUserAgentValue(context.getPackageName());
        this.mapView = mapView;
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
    }

    public void addMarkerWithGeofences(Context context, double latitude, double longitude, double outerGeofenceRadius, double innerGeofenceRadius) {
        clearGeofencesAndMarker();

        GeoPoint markerPoint = new GeoPoint(latitude, longitude);

        outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerGeofenceRadius));
        outerGeofence.setFillColor(Color.argb(102, 154, 220, 241));
        outerGeofence.setStrokeColor(Color.rgb(80, 156, 180));
        outerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(outerGeofence);

        innerGeofence = new Polygon();
        innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerGeofenceRadius));
        innerGeofence.setFillColor(Color.argb(50, 0, 255, 0));
        innerGeofence.setStrokeColor(Color.rgb(91, 206, 137));
        innerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(innerGeofence);

        // Create and configure the marker
        marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);

        // Load custom marker icon
        Bitmap customBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_loc);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(customBitmap, 34, 55, false);

        Drawable customDrawable = new BitmapDrawable(context.getResources(), resizedBitmap);

        // Set the custom Drawable as the icon for the marker
        marker.setIcon(customDrawable);

        mapView.getOverlays().add(marker);
    }

    public void updateGeofences(GeoPoint markerPoint, double outerGeofenceRadius, double innerGeofenceRadius) {
        if (outerGeofence != null && innerGeofence != null && marker != null) {
            // Update outer geofence
            outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerGeofenceRadius));

            // Update inner geofence
            innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerGeofenceRadius));


            mapView.invalidate();
        }
    }

    private void clearGeofencesAndMarker() {
        mapView.getOverlayManager().remove(outerGeofence);
        mapView.getOverlayManager().remove(innerGeofence);
        mapView.getOverlays().remove(marker);
        mapView.invalidate();
    }




    public void updateOuterGeofenceColor(Context context, boolean isExitMode) {
        if (outerGeofence != null) {
            int fillColor;
            int strokeColor;
            if (isExitMode) {
                // Set the color to F1D99A with 40% opacity for exit mode
                mapView.getOverlays().remove(marker);
                fillColor = Color.argb(102, 241, 217, 154);
                strokeColor = Color.argb(255, 180, 158, 80);
                Bitmap customBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_loc_exit);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(customBitmap, 34, 55, false);

                Drawable customDrawable = new BitmapDrawable(context.getResources(), resizedBitmap);

                // Set the custom Drawable as the icon for the marker
                marker.setIcon(customDrawable);

                mapView.getOverlays().add(marker);
            } else {
                // Set the original color for entry mode
                mapView.getOverlays().remove(marker);
                fillColor = Color.argb(102, 154, 220, 241);
                strokeColor = Color.rgb(80, 156, 180);
                Bitmap customBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_loc);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(customBitmap, 34, 55, false);

                Drawable customDrawable = new BitmapDrawable(context.getResources(), resizedBitmap);

                // Set the custom Drawable as the icon for the marker
                marker.setIcon(customDrawable);

                mapView.getOverlays().add(marker);
            }

            outerGeofence.setFillColor(fillColor);
            outerGeofence.setStrokeColor(strokeColor);
            mapView.invalidate();
        }
    }
}
