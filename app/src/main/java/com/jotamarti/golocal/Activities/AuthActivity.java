package com.jotamarti.golocal.Activities;

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
    private CheckBox checkBoxShop;
    private Button btnAuthActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initializeUI();
        initializeViewModel();
        setCredentialsIfExists();

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

    private void tryToLoginUser() {
        authActivityViewModel.loginUserInAuthService();
    }

    private void manageAuthErrors() {
        authActivityViewModel.getAuthError().observe(this, (AuthErrors authError) -> {
            switch (authError) {
                case WRONG_PASSWORD:
                    if (checkBoxRegister.isChecked()) {
                        // Manage a user trying to register, if we enter here it means that the user exists
                        CustomToast.showToast(AuthActivity.this, "El usuario ya existe", CustomToast.mode.SHORTER);
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
        startActivity(intent);
    }

    private void observeFirebaseUid() {
        // We enter here only if the user login correctly
        authActivityViewModel.getFirebaseUserUid().observe(this, (String userUid) -> {
            if (!userUid.isEmpty()) {
                if (!checkBoxRegister.isChecked()) {
                    getUserFromBackend(userUid);
                } else {
                    // We enter here in the remote case that a user trying to register puts the same password as an existing user
                    CustomToast.showToast(AuthActivity.this, "El usuario ya existe", CustomToast.mode.SHORTER);
                }
            }
        });
    }

    private void getUserFromBackend(String userUid) {
        if (checkBoxShop.isChecked()) {
            authActivityViewModel.getNewShop(userUid);
        } else {
            authActivityViewModel.getNewClient(userUid);

        }
    }

    private void observeUserFromBackend() {
        authActivityViewModel.getCurrentUser().observe(this, (User currentUser) -> {
            showMainActivity(currentUser);
        });
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
        observeFirebaseUid();
        observeUserFromBackend();
        manageAuthErrors();
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
    }

    // Validations
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= authActivityViewModel.MIN_PASS_LENGTH;
    }

}