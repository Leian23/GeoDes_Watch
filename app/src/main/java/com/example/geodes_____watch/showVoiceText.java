package com.example.geodes_____watch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.geodes_____watch.AlertSection.addAlertActivity;
import com.example.geodes_____watch.MapSection.map_activity;

import java.util.ArrayList;

public class showVoiceText extends ComponentActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_KEYBOARD = 1;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_voice_text);

        ImageButton voiceSearchButton = findViewById(R.id.voiceSearchButton);
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate voice search
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
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result_text", resultText);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
                case SPEECH_REQUEST_CODE:
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        String spokenText = results.get(0);
                        Intent speechResultIntent = new Intent();
                        speechResultIntent.putExtra("speech_result", spokenText);
                        setResult(RESULT_OK, speechResultIntent);
                        finish();
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





}

