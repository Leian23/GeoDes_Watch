package com.example.geodes_____watch;

import static com.google.android.gms.wearable.DataMap.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AuthActivity extends ComponentActivity {

    private boolean userLoggedIn = false;

    private String code;
    private String get_code;
    private FirebaseFirestore db;
    private String device;
    private RelativeLayout rlconnect, rlqrcode;
    private String useremail;
    private TextView txt_code;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = FirebaseFirestore.getInstance();

        rlconnect = findViewById(R.id.rlconnect);
        txt_code = findViewById(R.id.txt_code);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDevice();
                checkIfConnected();
            }
        }, 1000);
        rlconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDevice();

            }
        });

        rlqrcode = findViewById(R.id.rlqrcode);

        rlqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, GenerateQRCodeActivity.class);
                startActivity(intent);
            }
        });

    }

    public void checkIfConnected(){
        device = preferences.getString("deviceID", "");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                try {
                                    List<String> selectedItemsIds = (List<String>) document1.get("selectedDeviceID");
                                    for (String itemId : selectedItemsIds) {
                                        Log.d(TAG, "Scanning" + itemId);
                                        if(itemId.equalsIgnoreCase(device)){
                                            useremail = document1.getString("email");
                                            Constants.user_email = useremail;
                                            Log.d(TAG, "Email: " + useremail);
                                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                                catch (Exception ex){

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task1.getException());
                        }
                    }
                });

    }
    public void checkDevice(){
        device = preferences.getString("deviceID", "");
        if(device.equalsIgnoreCase("")){
            Log.d(TAG, "No Device Found Create One");
            addNewDevice();
        }
        else {
            DocumentReference docRef = db.collection("wareOS").document(device);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Device Found!" + document.getData());
                            get_code = document.getString("code");
                            txt_code.setText(document.getString("code"));
                            Log.d(TAG, "Code: " + document.getString("code"));
                            String is_loggedin = document.getString("is_loggedin");
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AuthActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("userLoggedIn", is_loggedin);
                            editor.putString("user_code", get_code);
                            Constants.user_code = get_code;
                            editor.apply();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            //checkIfConnected();
        }
    }

    public void addNewDevice(){

        code = getRandomString(6);
        String deviceName = Build.DEVICE;
        Map<String, Object> docData = new HashMap<>();
        docData.put("device_model", deviceName);
        docData.put("code", code);
        docData.put("is_loggedin", "false");


        DocumentReference key;
        key = db.collection("wareOS").document();
        String UniqueID = key.getId();

        db.collection("wareOS").document(UniqueID)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AuthActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("deviceID", UniqueID);
                        editor.putBoolean("userLoggedIn", true);
                        editor.apply();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Log.d(TAG, "DocumentSnapshot written with ID: " +  UniqueID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private static final String ALLOWED_CHARACTERS ="0123456789ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}

