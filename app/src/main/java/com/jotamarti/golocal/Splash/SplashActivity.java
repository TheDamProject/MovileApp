package com.jotamarti.golocal.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;

import com.jotamarti.golocal.Activities.AuthActivity;
import com.jotamarti.golocal.Activities.MainActivity;
import com.jotamarti.golocal.Utils.PrefsUtils;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPreferences = getSharedPreferences("authPreferences", Context.MODE_PRIVATE);

        Intent intentLogin = new Intent(this, AuthActivity.class);
        Intent intentMain = new Intent(this, MainActivity.class);

        //SystemClock.sleep(1000);
        if (!TextUtils.isEmpty(PrefsUtils.getEmailPrefs(loginPreferences)) && !TextUtils.isEmpty(PrefsUtils.getPasswordPrefs(loginPreferences))) {
            // TODO: Cambiar a main activity en produccion.
            startActivity(intentLogin);
        } else {
            startActivity(intentLogin);
        }
        finish();
    }
}