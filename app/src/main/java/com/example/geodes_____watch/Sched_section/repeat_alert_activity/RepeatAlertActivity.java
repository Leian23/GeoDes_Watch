package com.example.geodes_____watch.Sched_section.repeat_alert_activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.activity.ComponentActivity;
import com.example.geodes_____watch.R;
import com.example.geodes_____watch.Sched_section.addSched_activity;


public class RepeatAlertActivity extends ComponentActivity {
    private ToggleButton toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat, toggleSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_sched);

        // Initialize your ToggleButtons
        toggleMon = findViewById(R.id.toggleMon);
        toggleTue = findViewById(R.id.toggleTue);
        toggleWed = findViewById(R.id.toggleWed);
        toggleThu = findViewById(R.id.toggleThu);
        toggleFri = findViewById(R.id.toggleFri);
        toggleSat = findViewById(R.id.toggleSat);
        toggleSun = findViewById(R.id.toggleSun);

        ImageButton confirmButton = findViewById(R.id.confirmRepeat);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the next activity
                Intent intent = new Intent(RepeatAlertActivity.this, addSched_activity.class);

                // Pass the state of each ToggleButton to the next activity
                intent.putExtra("monday", toggleMon.isChecked());
                intent.putExtra("tuesday", toggleTue.isChecked());
                intent.putExtra("wednesday", toggleWed.isChecked());
                intent.putExtra("thursday", toggleThu.isChecked());
                intent.putExtra("friday", toggleFri.isChecked());
                intent.putExtra("saturday", toggleSat.isChecked());
                intent.putExtra("sunday", toggleSun.isChecked());

                // Start the next activity
                startActivity(intent);

            }
        });
    }
}
