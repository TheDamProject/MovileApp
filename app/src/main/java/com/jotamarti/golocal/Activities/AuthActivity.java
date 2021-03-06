package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.ViewModels.AuthActivityViewModel;

import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private final static int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    private final String TAG = "AuthActivity";

    // ViewModel
    private AuthActivityViewModel authActivityViewModel;

    // UI Views
    private EditText editTxtEmail;
    private EditText editTxtPassword;
    private TextView txtViewMessagePermission;
    private TextView txtViewLocationStatus;
    private TextView txtViewGettingLocation;
    private TextView txtViewSwitchShopText;
    private SwitchCompat switchRemember;
    private SwitchCompat switchRegister;
    private CheckBox checkBoxRegister;
    private Button btnAuthActivity;
    private Button btnGivePermission;
    private CircularProgressIndicator spinnerLoading;
    private ColorStateList textViewColor;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Object to get location data
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                manageUserInput(false);
            }
        });

        checkBoxRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAuthActivity.setText(R.string.authActivity_btn_register);
                } else {
                    btnAuthActivity.setText(R.string.authActivity_btn_login);
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

    @Override
    protected void onRestart() {
        manageUserInput(true);
        super.onRestart();
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
            // We only get here if the user is trying to log in in the application
            getUserFromBackend(userUid);
            authActivityViewModel.getFirebaseUserUid().removeObservers(this);
        });
    }

    private void getUserFromBackend(String userUid) {
        authActivityViewModel.getUserFromBackend(userUid);
        observeUserFromBackend();
    }

    private void observeUserFromBackend() {
        authActivityViewModel.getCurrentUser().observe(this, (User currentUser) -> {
            authActivityViewModel.user = currentUser;
            authActivityViewModel.getNearbyShops();
            observeListShopsFromBackend();
            authActivityViewModel.getCurrentUser().removeObservers(this);
        });
    }

    private void observeListShopsFromBackend() {
        authActivityViewModel.getNearbyShopsList().observe(this, (List<Shop> shopsNearby) -> {
            if (checkBoxRegister.isChecked()) {
                showConfigActivity();
            } else {
                showMainActivity();
            }
            authActivityViewModel.getNearbyShopsList().removeObservers(this);
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
                    manageUserInput(true);
                    break;
                case EMAIL_NOT_FOUND:
                    if (checkBoxRegister.isChecked()) {
                        // If we reach this point trying to register a new user it means that the email is available to the new user
                        authActivityViewModel.getNearbyShops();
                        observeListShopsFromBackend();
                    } else {
                        CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_not_found), CustomToast.mode.SHORTER);
                        manageUserInput(true);
                    }
                    break;
                default:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                    manageUserInput(true);
                    break;
            }
        });
    }

    private void manageBackendErrors() {
        authActivityViewModel.getClientBackendError().observe(this, (BackendErrors error) -> {
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
            manageUserInput(true);
        });
        authActivityViewModel.getUserBackendError().observe(this, (BackendErrors error) -> {
            Log.d(TAG, "Ejecutando el userBackendError");
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
            manageUserInput(true);
        });
    }

    private void showConfigActivity() {
        if (switchRemember.isChecked()) {
            authActivityViewModel.setPreferences();
        }
        Intent intent;
        if (switchRegister.isChecked()) {
            intent = new Intent(AuthActivity.this, ShopConfigurationActivity.class);
            intent.putExtra("cordinates", authActivityViewModel.userCoordinates);
        } else {
            intent = new Intent(AuthActivity.this, ClientConfigurationActivity.class);
        }
        intent.putExtra("email", authActivityViewModel.getCurrentInsertedEmail());
        intent.putExtra("password", authActivityViewModel.getCurrentInsertedPassword());
        intent.putExtra("userCoordinates", authActivityViewModel.userCoordinates);
        intent.putParcelableArrayListExtra("nearbyShops", authActivityViewModel.getNearbyShopsList().getValue());
        intent.putExtra("caller", "AuthActivity");
        startActivity(intent);
    }

    private void showMainActivity() {
        if (switchRemember.isChecked()) {
            authActivityViewModel.setPreferences();
        }
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("test", authActivityViewModel.getNearbyShopsList().getValue());
        intent.putExtra("user", authActivityViewModel.user);
        intent.putExtra("test", bundle);
        intent.putExtra("caller", "AuthActivity");
        intent.putExtra("userCoordinates", authActivityViewModel.userCoordinates);
        intent.putParcelableArrayListExtra("nearbyShops", authActivityViewModel.getNearbyShopsList().getValue());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            btnAuthActivity.setEnabled(false);
        } else {
            handlePermissionGranted();
        }
    }

    public void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    private void handlePermissionGranted() {
        if (!isLocationEnabled()) {
            CustomToast.showToast(this, getText(R.string.authActivity_textView_enableLocation).toString(), CustomToast.mode.LONGER);
            txtViewLocationStatus.setVisibility(View.VISIBLE);
            btnAuthActivity.setEnabled(false);
        }
        btnGivePermission.setVisibility(View.INVISIBLE);
        txtViewMessagePermission.setVisibility(View.INVISIBLE);
        getUserCoordinates();
    }

    private void handleLocationFinded() {
        btnGivePermission.setEnabled(false);
        btnAuthActivity.setEnabled(true);
        spinnerLoading.setVisibility(View.INVISIBLE);
        btnGivePermission.setVisibility(View.INVISIBLE);
        txtViewMessagePermission.setVisibility(View.INVISIBLE);
        txtViewLocationStatus.setVisibility(View.INVISIBLE);
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
                    getUserCoordinates();
                } else {
                    CustomToast.showToast(this, getString(R.string.error_failed_get_privileges), CustomToast.mode.LONGER);
                }
            }
        }
    }

    private void getUserCoordinates() {
        if (!checkHaveLocationPermission()) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, null);
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    if(authActivityViewModel.userCoordinates == null){
                        authActivityViewModel.userCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
                        handleLocationFinded();
                    }
                }
            }
        });
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkHaveLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            if (locationAvailability.isLocationAvailable()) {
                spinnerLoading.setVisibility(View.VISIBLE);
                txtViewGettingLocation.setVisibility(View.VISIBLE);
                txtViewLocationStatus.setVisibility(View.INVISIBLE);
                txtViewMessagePermission.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            authActivityViewModel.userCoordinates = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            spinnerLoading.setVisibility(View.INVISIBLE);
            txtViewGettingLocation.setVisibility(View.INVISIBLE);
            txtViewMessagePermission.setVisibility(View.INVISIBLE);
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
            handleLocationFinded();
        }
    };


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
        setTitle(getString(R.string.authActivity_title));
        editTxtEmail = findViewById(R.id.AuthActivity_editText_email);
        editTxtPassword = findViewById(R.id.AuthActivity_editText_password);
        switchRemember = findViewById(R.id.AuthActivity_switch_remember);
        checkBoxRegister = findViewById(R.id.AuthActivity_checkBox_register);
        btnAuthActivity = findViewById(R.id.AuthActivity_btn);
        txtViewMessagePermission = findViewById(R.id.AuthActivity_txtView_askPermission);
        btnGivePermission = findViewById(R.id.AuthActivity_btn_askPermission);
        spinnerLoading = findViewById(R.id.NewPostActivity_spinner_loading);
        txtViewLocationStatus = findViewById(R.id.AuthActivity_textView_locationStatus);
        txtViewGettingLocation = findViewById(R.id.AuthActivity_textView_gettingLocation);
        switchRegister = findViewById(R.id.AuthActivity_switch_register);
        txtViewSwitchShopText = findViewById(R.id.AuthActivity_textView_switchShop);
        textViewColor = txtViewSwitchShopText.getTextColors();
        spinnerLoading.setVisibility(View.INVISIBLE);
        txtViewLocationStatus.setVisibility(View.INVISIBLE);
        txtViewGettingLocation.setVisibility(View.INVISIBLE);
    }

    public void manageUserInput(Boolean state) {
        btnAuthActivity.setEnabled(state);
        btnGivePermission.setEnabled(state);
        switchRemember.setEnabled(state);
        switchRegister.setEnabled(state);
        editTxtPassword.setEnabled(state);
        editTxtEmail.setEnabled(state);
        checkBoxRegister.setEnabled(state);
        if (state) {
            spinnerLoading.setVisibility(View.INVISIBLE);
            txtViewSwitchShopText.setTextColor(textViewColor.withAlpha(255));
        } else {
            spinnerLoading.setVisibility(View.VISIBLE);
            txtViewSwitchShopText.setTextColor(textViewColor.withAlpha(55));
        }
    }

    // Validations
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= authActivityViewModel.MIN_PASS_LENGTH;
    }

}