package com.example.geodes_____watch.MapSection.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;
    private static boolean shakeToDismissEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");

        // Retrieve the alarm ringtone URI from the intent
        Uri alarmRingtoneUri = intent.getParcelableExtra("ALARM_RINGTONE_URI");
        Log.d("AlarmReceiver", "Alarm ringtone URI: " + alarmRingtoneUri);

        // Check if the alarmRingtoneUri is null
        if (alarmRingtoneUri == null) {
            Log.e("AlarmReceiver", "Alarm ringtone URI is null");
            return;
        }

        // Retrieve the user's preference for vibration
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        shakeToDismissEnabled = sharedPreferences.getBoolean("shake_to_dismiss", true);
        Log.d("AlarmReceiver", "Shake to Dismiss Enabled: " + shakeToDismissEnabled);

        // Retrieve the user's preference for audio output
        String selectedAudioOutput = sharedPreferences.getString("audio_output", "both_speaker_and_headphones");

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, alarmRingtoneUri);
                mediaPlayer.setLooping(true);

                // Set the audio attributes based on the selected output
                if (selectedAudioOutput.equals("speaker")) {
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
                } else if (selectedAudioOutput.equals("headphones")) {
                    // Set audio attributes for headphones
                    // You may need to customize this based on your requirements
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
                } else if (selectedAudioOutput.equals("both_speaker_and_headphones")) {
                    // Set audio attributes for both speaker and headphones
                    // You may need to customize this based on your requirements
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
                }

                // Retrieve the user's preference for volume
                int volume = sharedPreferences.getInt("volume", 50); // Default volume if not set
                float volumeLevel = volume / 100.0f; // Convert to a float between 0.0 and 1.0
                mediaPlayer.setVolume(volumeLevel, volumeLevel);

                // Set up asynchronous preparation
                mediaPlayer.prepareAsync();

                // Set up a callback for when the preparation is completed
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d("AlarmReceiver", "MediaPlayer setup completed, starting playback");
                    mp.start();


                    // Vibrate if enabled
                    if (shakeToDismissEnabled) {
                        registerShakeDetector(context);
                    }

                    // You can also add code here to handle other actions when the alarm starts
                });

            } catch (IOException e) {
                Log.e("AlarmReceiver", "Error setting up MediaPlayer: " + e.getMessage());
                releaseMediaPlayer();
            }
        }
    }

    private void registerShakeDetector(Context context) {
        ShakeDetector shakeDetector = new ShakeDetector(context);
        shakeDetector.setOnShakeListener(() -> {
            // Shake detected, stop the alarm
            stopAlarm();
        });
        shakeDetector.start();
    }

    static void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            releaseMediaPlayer();
        }

        // Stop vibration if enabled
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
