package com.constitution.india;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.constitution.india.utils.AppPreferences;

public class ConstitutionApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences prefs = new AppPreferences(this);
        AppCompatDelegate.setDefaultNightMode(prefs.getThemeMode());
    }
}
