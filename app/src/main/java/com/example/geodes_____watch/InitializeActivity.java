package com.example.geodes_____watch;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.ComponentActivity;


public class InitializeActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initizalize_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);

        // Apply fade animation to each dot
        applyFadeAnimation(dot1);
        applyFadeAnimation(dot2);
        applyFadeAnimation(dot3);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(InitializeActivity.this, MainActivity.class));
                finish();
            }
        }, 5000);
    }


    private void applyFadeAnimation(View view) {
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.3f);
        fadeAnimator.setDuration(800);
        fadeAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        fadeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        fadeAnimator.start();
    }


}
