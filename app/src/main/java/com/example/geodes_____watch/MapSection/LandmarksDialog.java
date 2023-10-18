package com.example.geodes_____watch.MapSection;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.geodes_____watch.R;

import org.osmdroid.views.MapView;

public class LandmarksDialog extends Dialog {
    private MapView mapView;
    private CheckBox checkBoxRestaurants;
    private CheckBox checkBoxTerminals;
    private CheckBox checkBoxHotels;
    private CheckBox checkBoxMuseum;
    private CheckBox checkBoxParks;
    private ImageButton PreviousBack;

    private SharedPreferences sharedPreferences;

    public LandmarksDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarks_dialogue);

        checkBoxRestaurants = findViewById(R.id.checkBoxRestaurant);
        checkBoxTerminals = findViewById(R.id.checkBoxTerminals);
        checkBoxHotels = findViewById(R.id.checkBoxHotels);
        checkBoxMuseum = findViewById(R.id.checkBoxMuseum);
        checkBoxParks = findViewById(R.id.checkBoxParks);
        PreviousBack = findViewById(R.id.backspace);

        // Restoring the state of the checkboxes from SharedPreferences
        checkBoxRestaurants.setChecked(sharedPreferences.getBoolean("restaurants", false));
        checkBoxTerminals.setChecked(sharedPreferences.getBoolean("terminals", false));
        checkBoxHotels.setChecked(sharedPreferences.getBoolean("hotels", false));
        checkBoxMuseum.setChecked(sharedPreferences.getBoolean("museum", false));
        checkBoxParks.setChecked(sharedPreferences.getBoolean("parks", false));





        PreviousBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        checkBoxRestaurants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Restaurants checkbox
                sharedPreferences.edit().putBoolean("restaurants", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();

                } else {
                    // Clear restaurant markers from the map
                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                    // Clear or hide restaurant markers from the map
                }
            }
        });


        checkBoxTerminals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Terminals checkbox
                sharedPreferences.edit().putBoolean("terminals", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Terminals checkbox is checked
                    Toast.makeText(getContext(), "Terminals checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Terminals checkbox is unchecked
                    Toast.makeText(getContext(), "Terminals checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxHotels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Hotels checkbox
                sharedPreferences.edit().putBoolean("hotels", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Hotels checkbox is checked
                    Toast.makeText(getContext(), "Hotels checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Hotels checkbox is unchecked
                    Toast.makeText(getContext(), "Hotels checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxMuseum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Museum checkbox
                sharedPreferences.edit().putBoolean("museum", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Museum checkbox is checked
                    Toast.makeText(getContext(), "Museum checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Museum checkbox is unchecked
                    Toast.makeText(getContext(), "Museum checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxParks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sharedPreferences.edit().putBoolean("parks", isChecked).apply();

                if (isChecked) {
                    Toast.makeText(getContext(), "Parks checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Parks checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
