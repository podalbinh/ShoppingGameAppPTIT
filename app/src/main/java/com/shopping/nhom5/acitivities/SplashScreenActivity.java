package com.shopping.nhom5.acitivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        checkLanguage();
        isDarkModeEnabled();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void checkLanguage() {
        String lang = sp.getString("language", "en");
        setAppLanguage(this, lang);
    }

    private void isDarkModeEnabled() {
        boolean darkModePreference = sp.getBoolean("dark_theme", false);
        if(darkModePreference) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setAppLanguage(Context context, String languageCode) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(languageCode));
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }
}