package com.example.geodes_____watch;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class InitializeActivity extends ComponentActivity {

    private boolean userLoggedIn = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;
    private static final String TAG = "InitializeActivity";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
        // Check if the user is logged in in a loop
        startActivity(new Intent(InitializeActivity.this, AuthActivity.class));
        finish();

    }

    private void applyFadeAnimation(View view) {
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.3f);
        fadeAnimator.setDuration(800);
        fadeAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        fadeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        fadeAnimator.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothServerSocket != null) {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Bluetooth server socket: " + e.getMessage());
            }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Bluetooth socket: " + e.getMessage());
            }
        }
    }
}

