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
        startActivity(new Intent(InitializeActivity.this, MainActivity.class));
        finish();
        // Start Bluetooth server in a separate thread
        new Thread(this::startBluetoothServer).start();

        // Check if the user is logged in in a loop
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userLoggedIn) {
                    // User is logged in, finish the activity
                    //use this if bluetooth login is working
                    /*startActivity(new Intent(InitializeActivity.this, MainActivity.class));
                    finish();*/
                } else {
                    // User is not logged in, continue the loop
                    new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
                }
            }
        }, 1000);

    }

    private void applyFadeAnimation(View view) {
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.3f);
        fadeAnimator.setDuration(800);
        fadeAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        fadeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        fadeAnimator.start();
    }

    private void startBluetoothServer() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                Log.e(TAG, "Device doesn't support Bluetooth");
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled, request user to enable it
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request Bluetooth permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BT_PERMISSION);
                return;
            }

            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Infinix HOT 20", MY_UUID);
            bluetoothSocket = bluetoothServerSocket.accept();
            // Receive login credentials from mobile device
            InputStream inputStream = bluetoothSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            bytesRead = inputStream.read(buffer);
            String loginCredentials = new String(buffer, 0, bytesRead);
            String[] credentials = loginCredentials.split(":");
            String email = credentials[0];
            String password = credentials[1];
            // Authenticate user with Firebase Authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User login successful
                            Log.d(TAG, "User login successful");
                            userLoggedIn = true; // Set the userLoggedIn flag to true
                            // Close the Bluetooth socket after successful login
                            closeBluetoothSocket();
                        } else {
                            // User login failed
                            Log.e(TAG, "User login failed: " + task.getException().getMessage());
                        }
                    });

        } catch (IOException e) {
            Log.e(TAG, "Error accepting or reading Bluetooth connection: " + e.getMessage());
        }
    }

    private void closeBluetoothSocket() {
        try {
            if (bluetoothServerSocket != null) {
                bluetoothServerSocket.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth sockets: " + e.getMessage());
        }
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

    // Define constants for Bluetooth permission request
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BT_PERMISSION = 2;
}

