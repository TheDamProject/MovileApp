package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.ViewModels.AuthActivityViewModel;

public class AuthActivity extends AppCompatActivity {

    private final static int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    private final String TAG = "AuthActivity";

    // ViewModel
    private AuthActivityViewModel authActivityViewModel;

    // UI Views
    private EditText editTxtEmail;
    private EditText editTxtPassword;
    private TextView txtViewMessagePermission;
    private SwitchCompat switchRemember;
    private CheckBox checkBoxRegister;
    private CheckBox checkBoxShop;
    private Button btnAuthActivity;
    private Button btnGivePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initializeUI();
        initializeViewModel();
        setCredentialsIfExists();
        handleLocationPermission();

        btnAuthActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authActivityViewModel.setCurrentInsertedEmail(editTxtEmail.getText().toString());
                authActivityViewModel.setCurrentInsertedPassword(editTxtPassword.getText().toString());
                if (!isValidEmail(authActivityViewModel.getCurrentInsertedEmail())) {
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_invalid_email), CustomToast.mode.SHORTER);
                    return;
                }
                if (!isValidPassword(authActivityViewModel.getCurrentInsertedPassword())) {
                    CustomToast.showToast(AuthActivity.this, String.format(getString(R.string.error_short_password), authActivityViewModel.MIN_PASS_LENGTH), CustomToast.mode.SHORTER);
                    return;
                }
                tryToLoginUser();
                //TODO: Tendremos que desactivar el botón mientras consultamos en firebase
            }
        });

        checkBoxRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAuthActivity.setText(R.string.auth_btn_register);
                } else {
                    btnAuthActivity.setText(R.string.auth_btn_login);
                }
            }
        });

        btnGivePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLocationPermission();
            }
        });


    }


    private void tryToLoginUser() {
        if (checkBoxRegister.isChecked()) {
            authActivityViewModel.loginUserInAuthService("register");
        } else {
            authActivityViewModel.loginUserInAuthService("login");
        }
        observeFirebaseUid();
    }

    private void observeFirebaseUid() {
        authActivityViewModel.getFirebaseUserUid().observe(this, (String userUid) -> {
            Log.d(TAG, "Firebase UID incoming:" + userUid);
            if (!userUid.isEmpty()) {
                if (!checkBoxRegister.isChecked()) {
                    // If the user is not trying to register we ask the backend for that user
                    getUserFromBackend(userUid);
                } else {
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_already_use), CustomToast.mode.SHORTER);
                }
            }
            authActivityViewModel.getFirebaseUserUid().removeObservers(this);
        });
    }

    private void getUserFromBackend(String userUid) {
        authActivityViewModel.getUserFromBackend(userUid);
        observeUserFromBackend();
    }

    private void observeUserFromBackend() {
        authActivityViewModel.getCurrentUser().observe(this, (User currentUser) -> {
            showMainActivity(currentUser);
            authActivityViewModel.getCurrentUser().removeObservers(this);
        });
    }

    private void manageAuthErrors() {
        authActivityViewModel.getAuthError().observe(this, (AuthErrors authError) -> {
            switch (authError) {
                case WRONG_PASSWORD:
                    if (checkBoxRegister.isChecked()) {
                        // Manage a user trying to register, if we enter here it means that the user exists
                        CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_already_use), CustomToast.mode.SHORTER);
                    } else {
                        CustomToast.showToast(AuthActivity.this, getString(R.string.error_wrong_password), CustomToast.mode.SHORTER);
                    }
                    break;
                case EMAIL_NOT_FOUND:
                    if (checkBoxRegister.isChecked()) {
                        // If we reach this point trying to register a new user it means that the email is avaliable to the new user
                        showConfigActivity();
                    } else {
                        CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_not_found), CustomToast.mode.SHORTER);
                    }
                    break;
                default:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                    break;
            }
        });
    }

    private void manageBackendErrors() {
        authActivityViewModel.getBackendError().observe(this, (BackendErrors error) -> {
            switch (error) {
                case REDIRECTION:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                case CLIENT_ERROR:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                case SERVER_ERROR:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                default:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
            }
        });
    }

    private void showConfigActivity() {
        if (switchRemember.isChecked()) {
            authActivityViewModel.setPreferences();
        }
        Intent intent;
        if (checkBoxShop.isChecked()) {
            intent = new Intent(AuthActivity.this, ShopConfigurationActivity.class);
        } else {
            intent = new Intent(AuthActivity.this, ClientConfigurationActivity.class);
        }
        intent.putExtra("email", authActivityViewModel.getCurrentInsertedEmail());
        intent.putExtra("password", authActivityViewModel.getCurrentInsertedPassword());
        startActivity(intent);
    }





    private void showMainActivity(User user) {
        if (switchRemember.isChecked()) {
            authActivityViewModel.setPreferences();
        }
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", user);
        intent.putExtra("caller", "AuthActivity");
        startActivity(intent);
    }

    // Location
    private Boolean checkHaveLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void handleLocationPermission() {
        if (!checkHaveLocationPermission()) {
            requestPermission();
        } else {
            handlePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!checkHaveLocationPermission()) {
                        return;
                    }
                    handlePermissionGranted();
                } else {
                    CustomToast.showToast(this, getString(R.string.error_failed_get_privileges), CustomToast.mode.LONGER);
                }
            }
        }
    }

    private void handlePermissionGranted() {
        btnGivePermission.setEnabled(false);
        btnAuthActivity.setEnabled(true);
        btnGivePermission.setVisibility(View.INVISIBLE);
        txtViewMessagePermission.setVisibility(View.INVISIBLE);
    }

    public void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    // SharedPreferences
    private void setCredentialsIfExists() {
        authActivityViewModel.getSharedPreferences().observe(this, (UserPreferences userPreferences) -> {
            String userEmail = userPreferences.getEmail();
            String userPassword = userPreferences.getPassword();
            if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
                editTxtEmail.setText(userEmail);
                editTxtPassword.setText(userPassword);
            } else if (App.getAppInDevelopment()) {
                //TODO: Ojo con esto en producccion
                editTxtEmail.setText("test@test.com");
                editTxtPassword.setText("123456");
            }
        });
    }

    // ViewModel
    private void initializeViewModel() {
        authActivityViewModel = new ViewModelProvider(this).get(AuthActivityViewModel.class);
        manageAuthErrors();
        manageBackendErrors();
    }

    // Initialize views
    public void initializeUI() {
        setTitle(getString(R.string.auth_title));
        editTxtEmail = findViewById(R.id.AuthActivity_editText_email);
        editTxtPassword = findViewById(R.id.AuthActivity_editText_password);
        switchRemember = findViewById(R.id.AuthActivity_switch_remember);
        checkBoxRegister = findViewById(R.id.AuthActivity_checkBox_register);
        checkBoxShop = findViewById(R.id.AuthActivity_checkBox_Shop);
        btnAuthActivity = findViewById(R.id.AuthActivity_btn);
        txtViewMessagePermission = findViewById(R.id.AuthActivity_txtView_askPermission);
        btnGivePermission = findViewById(R.id.AuthActivity_btn_askPermission);
        btnAuthActivity.setEnabled(false);
    }

    // Validations
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= authActivityViewModel.MIN_PASS_LENGTH;
    }

}