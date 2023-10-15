package com.example.geodes_____watch;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_____watch.recentAlerts_Adapter.Adapter3;
import com.example.geodes_____watch.recentAlerts_Adapter.DataModel3;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ComponentActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1; // Add this constant



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set an OnClickListener for voiceSearchButton
        ImageButton voiceSearchButton = findViewById(R.id.voiceSearchButton);
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate voice search
                displaySpeechRecognizer();
            }
        });

        // Set an OnClickListener for textSearchButton
        ImageButton textSearchButton = findViewById(R.id.textSearchButton);
        textSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the keyboard
                launchKeyboard();
            }
        });

       ImageButton Scheduless = findViewById(R.id.Schedules);
        Scheduless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });


        ImageButton alerts = findViewById(R.id.Alerts);
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
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
}

