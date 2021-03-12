package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.ViewModels.ClientConfigurationViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class ClientConfigurationActivity extends AppCompatActivity {

    private final String TAG = "ClientConfigurationAct";
    private final static int PERMISSIONS_REQUEST_CAMERA = 1;

    private ImageView clientAvatar;
    private FloatingActionButton btnChoosePicture;
    private Button btnSave;
    private EditText clientNickName;

    private FirebaseUser firebaseUser;

    private TextWatcher textWatcher;
    private boolean avatarInserted = false;

    private String email;
    private String password;

    // ViewModel
    private ClientConfigurationViewModel clientConfigurationViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_configuration);

        Intent AuthActivityIntent = getIntent();

        email = AuthActivityIntent.getStringExtra("email");
        password = AuthActivityIntent.getStringExtra("password");

        initializeTextWatcher();
        initializeUi();
        initializeViewModel();

        btnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveCameraPermissions()) {
                    CropImage.startPickImageActivity(ClientConfigurationActivity.this);
                } else {
                    askCameraPermissions();
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MACHOOOOOOOOOOOOOOOOOOO");
                clientConfigurationViewModel.registerClientInAuthService(email, password);
            }
        });
    }

    private void initializeViewModel(){
        clientConfigurationViewModel = new ViewModelProvider(this).get(ClientConfigurationViewModel.class);
        manageBackendErrors();
        manageAuthServiceErrors();
        observeRegisteredUserInAuthService();
        observeRegisteredUserInBackend();
    }

    private void observeRegisteredUserInAuthService(){
        Log.d(TAG, "Observando userFirebase");
        clientConfigurationViewModel.getAuthUser().observe(this, (FirebaseUser firebaseUser) -> {
            this.firebaseUser = firebaseUser;
            Log.d(TAG, "Me ha llegado el user de firebase");
            // Si llegamos aqui hemos creado correctamente el usuario en firebase. Ahora tenemos que crearlo en nuestro backend.
            clientConfigurationViewModel.registerClientInBackend(firebaseUser.getUid());

        });
    }

    private void observeRegisteredUserInBackend(){
        clientConfigurationViewModel.getClient().observe(this, (User client) -> {
            // Si llegamos aqui hemos creado el cliente en el backend, por tanto podemos pasar al mainActivity
            showMainActivity(client);
        });
    }

    private void showMainActivity(User client) {
        Intent intent = new Intent(ClientConfigurationActivity.this, MainActivity.class);
        intent.putExtra("user", client);
    }

    private void manageBackendErrors(){
        clientConfigurationViewModel.getBackendError().observe(this, (BackendErrors error) -> {
            switch (error){
                case REDIRECTION:
                    CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
                case CLIENT_ERROR:
                    CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
                case SERVER_ERROR:
                    CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
                default:
                    CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
            }
            // If we reach this point we delete the user in firebase becasue we are not able to regsiter it in our backend
            firebaseUser.delete();
        });
    }

    private void manageAuthServiceErrors(){
        clientConfigurationViewModel.getAuthError().observe(this, (AuthErrors error) -> {
            CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
        });
    }

    private void initializeUi(){
        setTitle("User Configuration");
        clientAvatar = findViewById(R.id.ClientConfigurationActivity_imageView_userAvatar);
        btnChoosePicture = findViewById(R.id.ClientConfigurationActivity_btn_choosePicture);
        btnSave = findViewById(R.id.ClientConfigurationActivity_btn_save);
        clientNickName = findViewById(R.id.ClientConfigurationActivity_editText_userNickname);

        clientNickName.addTextChangedListener(textWatcher);

        btnSave.setEnabled(false);
    }

    private Boolean haveCameraPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void askCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Aqui entra despues de que aceptemos la foto que hemos hecho y nos abre el activity de crop
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            startCrop(imageUri);
        }
        // Aqui entra cuando le damos aceptar en la pantalla de crop
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    Bitmap imagen = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    imagen.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    checkAllDataInserted();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                clientAvatar.setImageURI(result.getUri());
                avatarInserted = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(4, 4).setCropShape(CropImageView.CropShape.OVAL).start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(ClientConfigurationActivity.this);
            } else {
                CustomToast.showToast(this, "Fail to get permission", CustomToast.mode.LONGER);
            }
        }
    }

    private void checkAllDataInserted() {
        if (clientNickName.getText().toString().length() > 0 && avatarInserted) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    private void initializeTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllDataInserted();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}