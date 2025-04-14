package com.shopping.nhom5.frags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.shopping.nhom5.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference languagePreference = findPreference("language");
        languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            setAppLanguage(requireContext(), newValue.toString());
            refreshActivity();
            return true;
        });

        // Attach an OnPreferenceChangeListener to the dark mode preference
        SwitchPreferenceCompat darkModePreference = findPreference("dark_theme");
        if (darkModePreference != null) {
            darkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Toggle dark mode
                boolean isDarkModeEnabled = (boolean) newValue;
                setDarkMode(isDarkModeEnabled);

                return true;
            });
        }
    }

    private void setAppLanguage(Context context, String languageCode) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        // Set the language
        configuration.setLocale(new Locale(languageCode));

        // Update the resources
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Save the selected language in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }

    // Method to set dark mode
    private void setDarkMode(boolean enableDarkMode) {
        int nightMode = enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    // Method to refresh the activity by recreating it
    private void refreshActivity() {
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }
}