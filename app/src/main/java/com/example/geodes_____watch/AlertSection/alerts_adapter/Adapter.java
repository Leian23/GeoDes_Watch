package com.example.geodes_____watch.AlertSection.alerts_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.AlertSection.AlertsActivity;
import com.example.geodes_____watch.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class Adapter extends WearableRecyclerView.Adapter<Adapter.ViewHolder> {
    private List<DataModel> dataList;
    private Context context;
    private OnItemClickListener listener;
    private FirebaseFirestore firestore;

    private AlertsActivity alertsActivity;

    public Adapter(List<DataModel> dataList, Context context, FirebaseFirestore firestore, AlertsActivity alertsActivity) {
        this.dataList = dataList;
        this.context = context;
        this.firestore = firestore;
        this.alertsActivity = alertsActivity;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.bind(data);

        holder.alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {


            if(isChecked) {
                GeoPoint Point = new GeoPoint(data.getLatitude(), data.getLongitude());

                if (data.getEntryExit()) {
                    alertsActivity.addGeofence(Point, data.getOuterRadius(), data.getOuterCode(), data.getTitleAlerts(),true);
                    alertsActivity.addGeofence(Point, data.getInnerradius(), data.getInnerCode(), data.getTitleAlerts(), true);

                    Log.d("GeofenceLog", "Adding outer geofence: " +
                            "Point: " + Point +
                            ", OuterRadius: " + data.getOuterRadius() +
                            ", OuterCode: " + data.getOuterCode() +
                            ", AlertName: " + data.getTitleAlerts() +
                            ", Enabled: true");

                    Log.d("GeofenceLog", "Adding inner geofence: " +
                            "Point: " + Point +
                            ", OuterRadius: " + data.getInnerradius() +
                            ", InnerCode: " + data.getInnerCode() +
                            ", AlertName: " + data.getTitleAlerts() +
                            ", Enabled: true");


                } else {
                   alertsActivity.addGeofence(Point, data.getOuterRadius(), data.getExitCode(), data.getTitleAlerts(),false);

                    Log.d("GeofenceLog", "Adding outer geofence: " +
                            "Point: " + Point +
                            ", OuterRadius: " + data.getOuterRadius() +
                            ", OuterCode: " + data.getExitCode() +
                            ", AlertName: " + data.getTitleAlerts() +
                            ", Enabled: true");
                }

            } else if (!isChecked) {
                if (data.getEntryExit()) {
                    alertsActivity.clearGeo(data.getInnerCode(), data.getOuterCode());
                } else {
                    alertsActivity.removeGeofence(data.getExitCode());
                }
            }
            // Save the updated state to Firestore or perform other actions


            data.setAlertSwitch1(isChecked);
            updateAlertSwitchInFirestore(data.getTitleAlerts(), isChecked);
        });

        holder.itemView.setOnClickListener(view -> {
            // Handle item click here
            Toast.makeText(context, "Clicked: " + data.getTitleAlerts(), Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onItemClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder {
        TextView TitleAlerts;
        TextView NotesAlerts;
        ImageView Imgcalendar;
        TextView repeatDescription;
        ImageView pinIcon;

        TextView ListAlarms;
        Switch alertSwitch;

        ImageView alertPref;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleAlerts = itemView.findViewById(R.id.TitleAlert);
            NotesAlerts = itemView.findViewById(R.id.NotesAlert);
            Imgcalendar = itemView.findViewById(R.id.calendarImage);
            repeatDescription = itemView.findViewById(R.id.Repeat);
            pinIcon = itemView.findViewById(R.id.pinalerts);
            ListAlarms = itemView.findViewById(R.id.alarmLists);
            alertSwitch = itemView.findViewById(R.id.AlertSwitch);
            alertPref = itemView.findViewById(R.id.AlertPreference);
        }

        public void bind(DataModel data) {
            TitleAlerts.setText(data.getTitleAlerts());
            NotesAlerts.setText(data.getNotesAlerts());
            Imgcalendar.setImageResource(data.getImgcalendar());

            pinIcon.setImageResource(data.getPinIcon());

            alertPref.setImageResource(data.getAlertPref());
            alertSwitch.setChecked(data.getAlertSwitch1());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DataModel data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void updateAlertSwitchInFirestore(String alertName, boolean isChecked) {
        // Update both "geofencesEntry" and "geofencesExit" collections

        updateSwitchState("geofencesEntry", alertName, isChecked);
        updateSwitchState("geofencesExit", alertName, isChecked);
    }

    private void updateSwitchState(String collectionName, String alertName, boolean isChecked) {
        firestore.collection(collectionName)
                .whereEqualTo("alertName", alertName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Update the "alertEnabled" field with the new switch state
                            firestore.collection(collectionName)
                                    .document(document.getId())
                                    .update("alertEnabled", isChecked)
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                    });
                        }
                    } else {
                        // Handle failure or document not found
                    }
                });
    }
}
