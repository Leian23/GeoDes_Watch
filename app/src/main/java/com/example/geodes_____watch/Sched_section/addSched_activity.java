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
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceHelper;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.list_alerts;
import com.example.geodes_____watch.R;

import com.example.geodes_____watch.Sched_section.repeat_alert_activity.RepeatAlertActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedules_layout);

        scheduleTitle = findViewById(R.id.ScheduleTitle);

        voiceSearchButton = findViewById(R.id.voiceSearchButton);


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
                    String currentUser = "yow@gmail.com";
                    saveSchedToFirestore(currentUser);

                    Intent intent = new Intent(addSched_activity.this, ScheduleActivity.class);
                    startActivity(intent);
                }
            }
        });

        addAlert = findViewById(R.id.ChooseAlert);
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addSched_activity.this, list_alerts.class);
                startActivity(intent);
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
            }
        });

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
        currentUser = "yow@gmail.com";

        // Retrieve the values from the Intent
        Intent intent = getIntent();
        boolean monday = intent.getBooleanExtra("monday", false);
        boolean tuesday = intent.getBooleanExtra("tuesday", false);
        boolean wednesday = intent.getBooleanExtra("wednesday", false);
        boolean thursday = intent.getBooleanExtra("thursday", false);
        boolean friday = intent.getBooleanExtra("friday", false);
        boolean saturday = intent.getBooleanExtra("saturday", false);
        boolean sunday = intent.getBooleanExtra("sunday", false);

        String geofenceId = geofenceHelper.generateRequestId();
        Boolean SchedStat = false;

        TextView schedNameVariable = findViewById(R.id.ScheduleTitle);
        String schedName = schedNameVariable.getText().toString();

        Button txtTimePicker = findViewById(R.id.PickTime);
        String selectedTime = txtTimePicker.getText().toString();

        Map<String, Object> geofenceData = new HashMap<>();
        geofenceData.put("Sched", schedName);
        geofenceData.put("Time", selectedTime);
        geofenceData.put("Monday", monday);
        geofenceData.put("Tuesday", tuesday);
        geofenceData.put("Wednesday", wednesday);
        geofenceData.put("Thursday", thursday);
        geofenceData.put("Friday", friday);
        geofenceData.put("Saturday", saturday);
        geofenceData.put("Sunday", sunday);
        geofenceData.put("Email", currentUser);
        geofenceData.put("uniqueID", geofenceId);
        geofenceData.put("SchedStat", SchedStat);

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

    }




