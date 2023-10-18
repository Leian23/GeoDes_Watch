package com.example.geodes_____watch.Sched_section;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.add_alertt_recycle_view.list_alerts;
import com.example.geodes_____watch.Sched_section.repeat_alert_activity.RepeatAlertActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class addSched_activity extends ComponentActivity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1;
    private Button addAlert;
    private ImageButton voiceSearchButton;
    private ImageButton textSearchButton;
    private ImageButton discardAddSched;
    private Button btnPickTime;
    private Button RepeatSchedd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedules_layout);

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
                    sendComment(resultText);
                    break;
                case SPEECH_REQUEST_CODE:
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        String spokenText = results.get(0);
                        // Perform search using the interpreted text
                        // You can use the Google Places API here
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

    private void sendComment(String comment) {
        // Implement your logic for handling the result text from the keyboard
        // This method is invoked when the keyboard activity returns a result

        // For example, show a Toast with the entered text
        Toast.makeText(this, "Entered text: " + comment, Toast.LENGTH_SHORT).show();
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




}
