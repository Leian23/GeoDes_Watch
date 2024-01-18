package com.example.geodes_____watch.Sched_section.schedule_items;
import static com.google.android.gms.wearable.DataMap.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.ScheduleActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class Adapter1 extends WearableRecyclerView.Adapter<Adapter1.ViewHolder> {
    private List<DataModel1> dataList;
    private Context context;
    private OnItemClickListener listener;
    private FirebaseFirestore firestore;
    private ScheduleActivity scheduleActivity;

    public Adapter1(List<DataModel1> dataList, Context context, FirebaseFirestore firestore, ScheduleActivity scheduleActivity) {
        this.dataList = dataList;
        this.context = context;
        this.firestore = firestore;
        this.scheduleActivity = scheduleActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel1 data = dataList.get(position);

        holder.titleSetSched.setText(data.getSchedTitle());
        holder.timeSched.setText(data.getTimeStart());
        holder.daySched.setText(data.getSchedules());

        // Set an OnCheckedChangeListener for the switch
        holder.alertSwitch.setOnCheckedChangeListener(null); // Remove previous listener to avoid conflicts

        // Set the switch state based on the data
        holder.alertSwitch.setChecked(data.isAlertSwitchOn());

        // Set a new OnCheckedChangeListener for the switch
        holder.alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setAlertSwitchOn(isChecked);
                if (isChecked) {
                    Toast.makeText(context, "" + data.getSelectedItemsIds(), Toast.LENGTH_SHORT).show();

                    compareIdsAndRetrieveAlertName(data.getSelectedItemsIds());
                } else {
                    Log.e(TAG, "ie false");
                }

                // Update Firestore with the new switch state
                updateFirestoreWithSwitchState(data);
            }
        });
        holder.bind(data);

        // You may need to handle onClickListeners for other interactive elements
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click here
                //Toast.makeText(context, "Clicked: " + data.getSchedTitle(), Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onItemClick(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder {
        TextView timeSched;
        TextView alarmsSaved;
        TextView titleSetSched;
        Switch alertSwitch;
        ImageButton entryImageButton;
        TextView daySched;
        ImageView clockIc;
        ImageView calendarIc;

        ImageView alarmIc;

        TextView alarmList;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSched = itemView.findViewById(R.id.TimeSched);
            alarmsSaved = itemView.findViewById(R.id.alarmsSaved);
            titleSetSched = itemView.findViewById(R.id.TitleSetSched);
            alertSwitch = itemView.findViewById(R.id.AlertSwitch);
            entryImageButton = itemView.findViewById(R.id.Alerts);
            clockIc = itemView.findViewById(R.id.clockSchedIc);
            calendarIc = itemView.findViewById(R.id.calendarImage);
            alarmIc = itemView.findViewById(R.id.AlarmIc);
            daySched = itemView.findViewById(R.id.DaySched);
            alarmList = itemView.findViewById(R.id.alarmsSaved);

        }

        public void bind(DataModel1 data) {
            timeSched.setText(data.getTimeStart());
            alarmsSaved.setText(data.getSchedAlarms());
            titleSetSched.setText(data.getSchedTitle());
            alertSwitch.setChecked(data.isAlertSwitchOn());
            alarmIc.setImageResource(data.getIconMarker());
            clockIc.setImageResource(data.getIconCal());
            calendarIc.setImageResource(data.getIconCal());
            daySched.setText(data.getSchedules());
            alarmList.setText(data.getConcatenatedAlertNames());
        }
    }



    public interface OnItemClickListener {
        void onItemClick(DataModel1 data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void updateFirestoreWithSwitchState(DataModel1 data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("geofenceSchedule")
                .document(data.getUniqueId()) // Assuming you have a unique ID for each document
                .update("SchedStat", data.isAlertSwitchOn())
                .addOnSuccessListener(aVoid -> {
                    //Toast.makeText(context, "Switch state updated in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error updating switch state in Firestore", Toast.LENGTH_SHORT).show();
                });
    }
    private void compareIdsAndRetrieveAlertName(List<String> selectedIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String id : selectedIds) {
            // Compare with geofencesEntry
            db.collection("geofencesEntry")
                    .whereEqualTo("uniqueID", id)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String alertName = documentSnapshot.getString("alertName");
                            if (alertName != null) {
                                //Toast.makeText(context, "Match found in geofencesEntry. AlertName: " + alertName, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Match found in geofencesEntry. AlertName: " + alertName);
                                return; // Match found, no need to check further
                            }
                        }

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error querying geofencesEntry", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
