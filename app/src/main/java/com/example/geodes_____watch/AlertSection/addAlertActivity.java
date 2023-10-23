package com.example.geodes_____watch.AlertSection;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.ComponentActivity;
import androidx.cardview.widget.CardView;

import com.example.geodes_____watch.AlertSection.alerts_adapter.Adapter;
import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel;
import com.example.geodes_____watch.MapSection.create_geofence_functions.GeofenceSetup;
import com.example.geodes_____watch.MapSection.map_activity;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.showVoiceText;

import java.util.ArrayList;

public class addAlertActivity extends ComponentActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1; // Add this constant
    private CardView addANote;
    private ToggleButton toggleButton;
    private boolean isEntryMode = true;
    private GeofenceSetup geofenceSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alert_content);

        ImageButton voiceSearchButton = findViewById(R.id.voiceSearchButton);
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        ImageButton textSearchButton = findViewById(R.id.textSearchButton);
        textSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchKeyboard();
            }
        });

        addANote = findViewById(R.id.notesView);
        addANote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addAlertActivity.this, showVoiceText.class);
                startActivity(intent);
            }
        });


        // Inside setupSeekBarListeners method

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



    }






