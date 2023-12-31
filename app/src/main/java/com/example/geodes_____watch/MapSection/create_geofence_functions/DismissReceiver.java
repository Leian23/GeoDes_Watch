// DismissReceiver.java
package com.example.geodes_____watch.MapSection.create_geofence_functions;

import static com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceBroadcastReceiver.DISMISS_NOTIFICATION_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.geodes_____watch.ACTION_DISMISS_ALARM")) {
            // Add code here to handle the dismissal of the alarm
            Log.d("DismissReceiver", "Alarm dismissed");

            // Stop the alarm when dismissed
            AlarmReceiver.stopAlarm();

            // Cancel the dismiss notification
            cancelDismissNotification(context);

            // You might want to send a broadcast or update UI to reflect that the alarm has been dismissed
            Intent dismissIntent = new Intent("com.example.geodes_____watch.ALARM_DISMISSED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(dismissIntent);
        }
    }

    private void cancelDismissNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(DISMISS_NOTIFICATION_ID);
    }
}