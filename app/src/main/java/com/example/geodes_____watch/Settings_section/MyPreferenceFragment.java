package com.example.geodes_____watch.Settings_section;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;


import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.geodes_____watch.R;

public  class MyPreferenceFragment extends PreferenceFragmentCompat {

    private static final String PREF_RINGTONE = "ringtone";
    private static final String KEY_SELECTED_RINGTONE_URI = "selected_ringtone_uri";
    private static final int REQUEST_RINGTONE_PICKER = 1;
    private static final String PREF_ALARM_RINGTONE = "alarm_ringtone";
    private static final String KEY_SELECTED_ALARM_RINGTONE_URI = "selected_alarm_ringtone_uri";

    private static final String PREF_DISTANCE_UNIT = "distance_unit_preference";
    private static final int REQUEST_ALARM_RINGTONE_PICKER = 2;
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Find the custom ringtone preference
        Preference ringtonePreference = findPreference("ringtone");

        // Set the click listener for the custom ringtone preference
        if (ringtonePreference != null) {
            ringtonePreference.setOnPreferenceClickListener(preference -> {
                // Launch the ringtone picker
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, REQUEST_RINGTONE_PICKER);
                return true;
            });
        }
        updateRingtonePreferenceSummary();
        updateAlarmRingtonePreferenceSummary();
        updateAlertTypePreferenceSummary();
        updateAudioOutputPreferenceSummary();


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            // Save the selected URI in SharedPreferences
            if (requestCode == REQUEST_RINGTONE_PICKER) {
                savePreference("selected_ringtone_uri", selectedUri);
                updateRingtonePreferenceSummary();
            }else if (requestCode == REQUEST_ALARM_RINGTONE_PICKER) {
                savePreference(KEY_SELECTED_ALARM_RINGTONE_URI, selectedUri);
                updateAlarmRingtonePreferenceSummary();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePreference(String key, Uri value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value != null ? value.toString() : null);
        editor.apply();
    }

    private void updateRingtonePreferenceSummary() {
        Preference ringtonePreference = findPreference("ringtone");
        if (ringtonePreference != null) {
            updatePreferenceSummary(ringtonePreference, "selected_ringtone_uri", "Ringtone");
        }
    }

    private void updateAlarmRingtonePreferenceSummary() {
        Preference alarmRingtonePreference = findPreference(PREF_ALARM_RINGTONE);
        if (alarmRingtonePreference != null) {
            updatePreferenceSummary(alarmRingtonePreference, KEY_SELECTED_ALARM_RINGTONE_URI, getString(R.string.alarm_ringtone_label));
        }
    }

    private void updatePreferenceSummary(Preference preference, String uriKey, String defaultSummary) {
        // Retrieve the saved URI
        String uriString = sharedPreferences.getString(uriKey, null);

        if (preference != null) {
            if (uriString != null) {
                Uri uri = Uri.parse(uriString);
                String name = getRingtoneName(uri);
                preference.setSummary((name != null ? name : "None"));
            } else {
                preference.setSummary("Select custom " + defaultSummary.toLowerCase());
            }
        }
    }

    private String getRingtoneName(Uri ringtoneUri) {
        if (ringtoneUri == null) {
            return null;
        }

        Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), ringtoneUri);
        if (ringtone != null) {
            return ringtone.getTitle(requireContext());
        }

        return null;
    }


    private void updateAlertTypePreferenceSummary() {
        ListPreference alertTypePreference = findPreference("alert_type");
        if (alertTypePreference != null) {
            String alertType = sharedPreferences.getString("alert_type", "outer Alert"); // Default to "outer Alert"
            alertTypePreference.setSummary(alertType);
        }
    }

    public String getAlertTypePreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("alert_type", "outer Alert");
    }

    private void updateAudioOutputPreferenceSummary() {
        ListPreference audioOutputPreference = findPreference("audio_output");
        if (audioOutputPreference != null) {
            updatePreferenceSummary(audioOutputPreference, "audio_output", getString(R.string.audio_output_label));
        }
    }


}

