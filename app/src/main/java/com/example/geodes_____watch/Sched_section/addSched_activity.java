package com.example.geodes_____watch.Sched_section;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.Constants;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.AlertAdapter5;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.DataModel5;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.list_alerts;
import com.example.geodes_____watch.R;

import com.example.geodes_____watch.Sched_section.repeat_alert_activity.RepeatAlertActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class addSched_activity extends ComponentActivity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1;
    private Button addAlert;
    private ImageButton voiceSearchButton;
    private ImageButton textSearchButton;
    private ImageButton discardAddSched;
    private Button btnPickTime;
    private Button RepeatSchedd;
    private TextView scheduleTitle;
    private GeofenceHelper geofenceHelper = new GeofenceHelper();
    boolean monday, tuesday,wednesday,thursday,friday,saturday,sunday;

    private String togmon = "", togtue = "", togwed = "", togthu = "", togfri = "", togsat = "", togsun = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedules_layout);

        scheduleTitle = findViewById(R.id.ScheduleTitle);

        voiceSearchButton = findViewById(R.id.voiceSearchButton);

        fetchAlertsFromFirestore();

        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate voice search
                displaySpeechRecognizer();
            }
        });

        textSearchButton = findViewById(R.id.textSearchButton);
        textSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the keyboard
                launchKeyboard();
            }
        });

        ImageButton saveButton = findViewById(R.id.confirmSched);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Name".equals(scheduleTitle.getText().toString())||"".equals(scheduleTitle.getText().toString())){
                    Toast.makeText(addSched_activity.this, "Please enter a schedule name", Toast.LENGTH_SHORT).show();
                }
                else if("Pick Time".equals(btnPickTime.getText().toString())){
                    Toast.makeText(addSched_activity.this, "Please select time", Toast.LENGTH_SHORT).show();
                }
                else {
                    //temporary constant variable, replace when auth is integrated
                    String currentUser = Constants.user_email;
                    saveSchedToFirestore(currentUser);

                    Intent intent = new Intent(addSched_activity.this, ScheduleActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        btnPickTime = findViewById(R.id.PickTime);
        btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        RepeatSchedd = findViewById(R.id.RepeatSched);
        RepeatSchedd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addSched_activity.this, RepeatAlertActivity.class);
                startActivity(intent);
                finish();
            }
        });
        RepeatSchedd.setText("Repeat");
        // Retrieve the values from the Intent
        Intent intent = getIntent();

        monday = intent.getBooleanExtra("monday", false);
        tuesday = intent.getBooleanExtra("tuesday", false);
        wednesday = intent.getBooleanExtra("wednesday", false);
        thursday = intent.getBooleanExtra("thursday", false);
        friday = intent.getBooleanExtra("friday", false);
        saturday = intent.getBooleanExtra("saturday", false);
        sunday = intent.getBooleanExtra("sunday", false);

        updateRepeatSchedButtonText(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        discardAddSched = findViewById(R.id.discardAdd);
        discardAddSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
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
                    scheduleTitle.setText(resultText);
                    break;
                case SPEECH_REQUEST_CODE:
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        String spokenText = results.get(0);
                        scheduleTitle.setText(spokenText);
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

    private void showTimePicker() {
        // Get the current time
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Convert 24-hour format to 12-hour format
                        int hourIn12Format = selectedHour % 12;
                        String amPm = (selectedHour >= 12) ? "PM" : "AM";

                        // Handle the selected time, e.g., update a TextView
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                        // textView.setText(selectedTime);
                        btnPickTime.setText(selectedTime);
                    }
                },
                hourOfDay,
                minute,
                false
        );

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    private void saveSchedToFirestore(String currentUser){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //temporary constant user, replace when auth is integrated
        currentUser = Constants.user_email;
        boolean monday2 = monday;
        boolean tuesday2 = tuesday;
        boolean wednesday2 = wednesday;
        boolean thursday2 = thursday;
        boolean friday2 = friday;
        boolean saturday2 = saturday;
        boolean sunday2 = sunday;


        String geofenceId = geofenceHelper.generateRequestId();
        Boolean SchedStat = false;

        TextView schedNameVariable = findViewById(R.id.ScheduleTitle);
        String schedName = schedNameVariable.getText().toString();

        Button txtTimePicker = findViewById(R.id.PickTime);
        String selectedTime = txtTimePicker.getText().toString();

        Map<String, Object> geofenceData = new HashMap<>();
        geofenceData.put("Sched", schedName);
        geofenceData.put("Time", selectedTime);
        geofenceData.put("Monday", monday2);
        geofenceData.put("Tuesday", tuesday2);
        geofenceData.put("Wednesday", wednesday2);
        geofenceData.put("Thursday", thursday2);
        geofenceData.put("Friday", friday2);
        geofenceData.put("Saturday", saturday2);
        geofenceData.put("Sunday", sunday2);
        geofenceData.put("Email", currentUser);
        geofenceData.put("uniqueID", geofenceId);
        geofenceData.put("SchedStat", SchedStat);

        RecyclerView recyclerViewSched = findViewById(R.id.item_alert_choose);
        AlertAdapter5 adapterSched = (AlertAdapter5) recyclerViewSched.getAdapter();

        // Include the selected items' unique IDs in your Firestore data
        Set<String> selectedItemsIds = adapterSched.getSelectedItemsIds();
        if (!selectedItemsIds.isEmpty()) {
            geofenceData.put("selectedItemsIds", new ArrayList<>(selectedItemsIds));
        }

        // Add the geofenceData to Firestore
        db.collection("geofenceSchedule")
                .document(geofenceId)
                .set(geofenceData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SchedUpload", "Successs");
                        Toast.makeText(addSched_activity.this, "Successfully adding "+schedName, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                    }
                });
        }
    private void updateRepeatSchedButtonText(boolean monday, boolean tuesday, boolean wednesday,
                                             boolean thursday, boolean friday, boolean saturday,
                                             boolean sunday) {
        StringBuilder buttonText = new StringBuilder("");

        if (monday) buttonText.append("Mon ");
        if (tuesday) buttonText.append("Tue ");
        if (wednesday) buttonText.append("Wed ");
        if (thursday) buttonText.append("Thu ");
        if (friday) buttonText.append("Fri ");
        if (saturday) buttonText.append("Sat ");
        if (sunday) buttonText.append("Sun ");

        // Set text based on selected days
        if (!buttonText.toString().equals("")) {
            RepeatSchedd.setText("Repeat");
        RepeatSchedd.setText(buttonText.toString());
        }
    }
    private void fetchAlertsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference geofenceEntryCollection = db.collection("geofencesEntry");
        CollectionReference geofenceExitCollection = db.collection("geofencesExit");

        // Combine the results of both collections
        geofenceEntryCollection.whereEqualTo("email", Constants.user_email).get().addOnSuccessListener(entrySnapshots -> {
            List<DataModel5> alertItems = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : entrySnapshots) {
                String alertName = documentSnapshot.getString("alertName");
                String uniqueId = documentSnapshot.getId();

                DataModel5 alertItem = new DataModel5(alertName, R.drawable.get_in, uniqueId);
                alertItems.add(alertItem);
            }

            // Fetch data from the "geofenceExit" collection
            geofenceExitCollection.whereEqualTo("email", Constants.user_email).get().addOnSuccessListener(exitSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : exitSnapshots) {
                    String alertName = documentSnapshot.getString("alertName");
                    String uniqueId = documentSnapshot.getId();

                    DataModel5 alertItem = new DataModel5(alertName, R.drawable.get_out, uniqueId);
                    alertItems.add(alertItem);
                }
                WearableRecyclerView recyclerVieww = findViewById(R.id.item_alert_choose);
                recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                AlertAdapter5 adapter1 = new AlertAdapter5(alertItems, this); // Fix the instantiation here
                recyclerVieww.setAdapter(adapter1);
                recyclerVieww.setEdgeItemsCenteringEnabled(false);


                // Set up RecyclerView with the combined data
                // setUpRecyclerView(alertItems);
            }).addOnFailureListener(e -> {
                // Handle errors
            });

        }).addOnFailureListener(e -> {
            // Handle errors
        });
    }
    }




