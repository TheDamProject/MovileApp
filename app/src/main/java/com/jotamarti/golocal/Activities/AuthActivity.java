package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.ViewModels.AuthActivityViewModel;

public class AuthActivity extends AppCompatActivity {

    private final String TAG = "AuthActivity";

    // ViewModel
    private AuthActivityViewModel authActivityViewModel;

    // UI Views
    private EditText editTxtEmail;
    private EditText editTxtPassword;
    private SwitchCompat switchRemember;
    private CheckBox checkBoxRegister;
    private Button btnAuthActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Deshabilitamos modo noche
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Iniciamos variables
        authActivityViewModel = new ViewModelProvider(this).get(AuthActivityViewModel.class);

        initializeUI();
        setCredentialsIfExists();
        manageBackendErrors();
        manageAuthErrors();

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
                if (checkBoxRegister.isChecked()) {
                    registerUser();
                } else {
                    loginUser();
                }
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


    }


    private void registerUser() {
        authActivityViewModel.registerUser();
        startObservingRegisteredUser();
    }

    private void startObservingRegisteredUser(){
        authActivityViewModel.getRegisteredUserUid().observe(this, (String userUid) -> {
            if(!userUid.isEmpty()) {
                getUserFromBackend(userUid);
            }
        });

    }

    private void loginUser() {
        authActivityViewModel.loginUser();
        startObservingLoggedUser();
    }

    private void startObservingLoggedUser(){
        authActivityViewModel.getLoggedUser().observe(this, (String userUid) -> {
            if(!userUid.isEmpty()) {
                getUserFromBackend(userUid);
            }
        });
    }

    private void getUserFromBackend(String userUid) {
        if (authActivityViewModel.getCurrentUser() == null) {
            authActivityViewModel.getNewUser(userUid);
            startObservingUser();
        } else {
            authActivityViewModel.getNewUser(userUid);
        }
    }

    private void startObservingUser() {
        authActivityViewModel.getCurrentUser().observe(this, (User currentUser) -> {
            showMainActivity(currentUser, getString(R.string.auth_action_login));
        });
    }

    private void manageBackendErrors() {
        authActivityViewModel.getBackendError().observe(this, (Integer currentError) -> {
            Log.d(TAG, "Error numero: " + String.valueOf(currentError));
        });
    }

    private void manageAuthErrors(){
        authActivityViewModel.getAuthError().observe(this, (AuthErrors authError) -> {
            switch (authError){
                case WRONG_PASSWORD:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_wrong_password), CustomToast.mode.SHORTER);
                    break;
                case EMAIL_NOT_FOUND:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_not_found), CustomToast.mode.SHORTER);
                    break;
                case GENERIC_LOGIN_ERROR:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                    break;
                case EMAIL_ALREADY_IN_USE:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_already_use), CustomToast.mode.SHORTER);
                    break;
                case GENERIC_REGISTER_ERROR:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
                    break;
                default:
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                    break;
            }
        });
    }

    private void showMainActivity(User user, String action) {
        if (action.equals(getString(R.string.auth_action_login))) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("avatar", user.getAvatar());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("uid", user.getUserId());
            startActivity(intent);
        } else {
            // Al ser un register tendremos que enviar al backend algo
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email", user.getEmail());
            startActivity(intent);
        }

    }

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

    public void initializeUI() {
        setTitle(getString(R.string.auth_title));
        editTxtEmail = findViewById(R.id.auth_activity_edit_text_email);
        editTxtPassword = findViewById(R.id.auth_activity_edit_text_password);
        switchRemember = findViewById(R.id.auth_activity_switch_remember);
        checkBoxRegister = findViewById(R.id.auth_activity_checkbox_register);
        btnAuthActivity = findViewById(R.id.auth_activity_btn);
    }

    // Validations
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= authActivityViewModel.MIN_PASS_LENGTH;
    }

}