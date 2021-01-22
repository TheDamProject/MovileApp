package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;

public class AuthActivity extends AppCompatActivity {

    private final int PASS_LENGTH = 6;
    private final String TAG = "AUTH_ACTIVITY";

    private EditText editTxtEmail;
    private EditText editTxtPassword;
    private Switch switchRemember;
    private CheckBox checkBoxRegister;
    private Button btnAuthActivity;

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("authPreferences", Context.MODE_PRIVATE);

        initializeUI();

        btnAuthActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTxtEmail.getText().toString();
                String password = editTxtPassword.getText().toString();
                if (!isValidEmail(email)) {
                    CustomToast.showToast(AuthActivity.this, getString(R.string.error_invalid_email), CustomToast.mode.SHORTER);
                    return;
                }
                if (!isValidPassword(password)) {
                    CustomToast.showToast(AuthActivity.this, String.format(getString(R.string.error_short_password), PASS_LENGTH), CustomToast.mode.SHORTER);
                    return;
                }
                if (checkBoxRegister.isChecked()) {
                    registerUser(email, password);
                } else {
                    loginUser(email, password);
                }
            }
        });

        checkBoxRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAuthActivity.setText("REGISTER");
                } else {
                    btnAuthActivity.setText("LOGIN");
                }
            }
        });

        setCredentialsIfExists();
    }

    public void initializeUI() {
        setTitle(getString(R.string.auth_title));
        editTxtEmail = findViewById(R.id.editTextEmail);
        editTxtPassword = findViewById(R.id.editTextPassword);
        switchRemember = findViewById(R.id.switchRemember);
        checkBoxRegister = findViewById(R.id.checkBoxRegister);
        btnAuthActivity = findViewById(R.id.btnAuth);
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (switchRemember.isChecked()) {
                                saveOnPreferences(email, password);
                            }
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            showMainActivity(user, getString(R.string.auth_action_register));
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                CustomToast.showToast(AuthActivity.this, getString(R.string.error_email_already_use), CustomToast.mode.SHORTER);
                            } catch (Exception e) {
                                CustomToast.showToast(AuthActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
                            }
                        }
                    }
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (switchRemember.isChecked()) {
                                saveOnPreferences(email, password);
                            }
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, user.getUid());
                            showMainActivity(user, getString(R.string.auth_action_login));
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                CustomToast.showToast(AuthActivity.this, getString(R.string.error_wrong_password), CustomToast.mode.SHORTER);
                            } catch (Exception e) {
                                CustomToast.showToast(AuthActivity.this, getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                            }
                        }
                    }
                });
    }

    private void showMainActivity(FirebaseUser user, String action) {
        if (action.equals(getString(R.string.auth_action_login))) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email", user.getEmail());
            startActivity(intent);
        } else {
            // Al ser un register tendremos que enviar al backend algo
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email", user.getEmail());
            startActivity(intent);
        }

    }

    private void saveOnPreferences(String email, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void setCredentialsIfExists() {
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        if (!email.isEmpty() && !password.isEmpty()) {
            editTxtEmail.setText(email);
            editTxtPassword.setText(password);
        }
    }

    // Validaciones
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= PASS_LENGTH;
    }
}