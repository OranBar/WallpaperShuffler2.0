package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        final Button clearDirs = findViewById(R.id.clear_dirs_btn);
//        clearDirs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearDirectories();
//            }
//        });
    }



    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference intervalTime = findPreference("interval_time");
            intervalTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Integer parsedIntValue = tryParseInt((String) newValue);
                    if (parsedIntValue != null) {
                        if (parsedIntValue >= 15) {
                            return true;
                        }
                        //Error. Number must be >=15
                        Toast startSequenceToast = Toast.makeText(getContext(), "Invalid Input: Number needs to be >=15", Toast.LENGTH_LONG);
                        startSequenceToast.show();

                        return false;
                    }
                    //Error. NonDigit characters
                    Toast startSequenceToast = Toast.makeText(getContext(), "Invalid Input: Only digits allowed", Toast.LENGTH_LONG);
                    startSequenceToast.show();

                    return false;
                }
            });


            Preference clearDirs = findPreference("clear_dirs_layout");

            clearDirs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    clearDirectories(getContext());
                    return false;
                }
            });
        }

        public static Integer tryParseInt(String someText) {
            try {
                return Integer.parseInt(someText);
            } catch (NumberFormatException ex) {
                return null;
            }
        }


        public static void clearDirectories(Context context) {
            WPDatabaseConnection db = new WPDatabaseConnection(context);
            db.clearAllDirectories();
        }

//        @Override
//        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//            super.onViewCreated(view, savedInstanceState);
//
////            LinearLayout clearDirectories_layout = view.findViewById(R.)
//        }
    }
}