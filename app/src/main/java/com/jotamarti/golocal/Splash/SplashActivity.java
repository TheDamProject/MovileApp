package com.jotamarti.golocal.Splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import com.jotamarti.golocal.Activities.AuthActivity;
import com.jotamarti.golocal.Activities.MainActivity;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.SharedPreferences.DataStorage;

public class SplashActivity extends AppCompatActivity {

    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // SharedPreferences
        dataStorage = new DataStorage(App.getContext());
        String userEmail = (String) dataStorage.read("email", DataStorage.STRING);
        String userPassword = (String) dataStorage.read("password", DataStorage.STRING);

        Intent intentLogin = new Intent(this, AuthActivity.class);
        Intent intentMain = new Intent(this, MainActivity.class);

        if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
            // TODO: Cambiar a main activity en produccion.
            startActivity(intentLogin);
        } else {
            startActivity(intentLogin);
        }
        finish();
    }
}