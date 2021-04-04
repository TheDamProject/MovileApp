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
import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.ImageUtil;
import com.jotamarti.golocal.ViewModels.ClientConfigurationViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ClientConfigurationActivity extends AppCompatActivity {

    private final String TAG = "ClientConfigurationAct";
    private final static int PERMISSIONS_REQUEST_CAMERA = 1;

    //Views
    private ImageView clientAvatar;
    private FloatingActionButton btnChoosePicture;
    private Button btnSave;
    private EditText clientNickName;

    private TextWatcher textWatcher;



    // ViewModel
    private ClientConfigurationViewModel clientConfigurationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_configuration);

        initializeViewModel();
        getDataFromAuthActivity();
        initializeTextWatcher();
        initializeUi();


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
                clientConfigurationViewModel.nickName = clientNickName.getText().toString();
                clientConfigurationViewModel.registerClientInAuthService(clientConfigurationViewModel.email, clientConfigurationViewModel.password);
                observeRegisteredUserInAuthService();
            }
        });
    }

    private void observeRegisteredUserInAuthService(){
        clientConfigurationViewModel.getAuthUser().observe(this, (FirebaseUser firebaseUser) -> {
            clientConfigurationViewModel.firebaseUser = firebaseUser;
            Log.d(TAG, "UID recibido por el observador " + firebaseUser.getUid());
            Log.d(TAG, "UID recibido en el ViewModel " + clientConfigurationViewModel.firebaseUser.getUid());
            // Si llegamos aqui hemos creado correctamente el usuario en firebase. Ahora tenemos que crearlo en nuestro backend.
            clientConfigurationViewModel.registerClientInBackend(firebaseUser.getUid(), clientConfigurationViewModel.imageBase64, clientConfigurationViewModel.nickName);
            observeRegisteredUserInBackend();
            clientConfigurationViewModel.getAuthUser().removeObservers(this);
        });
    }

    private void observeRegisteredUserInBackend(){
        clientConfigurationViewModel.getClient().observe(this, (User client) -> {
            showMainActivity(client);
            clientConfigurationViewModel.getClient().removeObservers(this);
        });
    }

    private void showMainActivity(User client) {
        Intent intent = new Intent(ClientConfigurationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", client);
        intent.putParcelableArrayListExtra("nearbyShops", clientConfigurationViewModel.nearbyShops);
        intent.putExtra("caller", "ClientConfigurationActivity");
        startActivity(intent);
    }

    // Errors handling
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
            // If we reach this point we delete the user in firebase because we are not able to register it in our backend
            Log.d(TAG, "El uid del cliente a borrar " + clientConfigurationViewModel.firebaseUser.getUid());
            clientConfigurationViewModel.firebaseUser.delete();
        });
    }

    private void manageAuthServiceErrors(){
        clientConfigurationViewModel.getAuthError().observe(this, (AuthErrors error) -> {
            CustomToast.showToast(ClientConfigurationActivity.this, getString(R.string.error_register_generic), CustomToast.mode.SHORTER);
        });
    }

    // Permission and camera handlers
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(ClientConfigurationActivity.this);
            } else {
                CustomToast.showToast(this, getString(R.string.error_failed_get_privileges), CustomToast.mode.LONGER);
            }
        }
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
                clientConfigurationViewModel.imageBase64 = ImageUtil.UriToBase64(uri);
                checkAllDataInserted();
                clientAvatar.setImageURI(result.getUri());
                clientConfigurationViewModel.avatarInserted = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(4, 4).setCropShape(CropImageView.CropShape.OVAL).start(this);
    }

    private void checkAllDataInserted() {
        if (clientNickName.getText().toString().length() > 0 && clientConfigurationViewModel.avatarInserted) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    // Initialization
    private void initializeUi(){
        setTitle(getString(R.string.ClientConfigurationActivity_title));
        clientAvatar = findViewById(R.id.ClientConfigurationActivity_imageView_userAvatar);
        btnChoosePicture = findViewById(R.id.ClientConfigurationActivity_btn_choosePicture);
        btnSave = findViewById(R.id.ClientConfigurationActivity_btn_save);
        clientNickName = findViewById(R.id.ClientConfigurationActivity_editText_userNickname);
        clientNickName.addTextChangedListener(textWatcher);
        btnSave.setEnabled(false);
    }


    private void initializeViewModel(){
        clientConfigurationViewModel = new ViewModelProvider(this).get(ClientConfigurationViewModel.class);
        manageBackendErrors();
        manageAuthServiceErrors();
    }

    private void getDataFromAuthActivity() {
        Intent AuthActivityIntent = getIntent();
        clientConfigurationViewModel.email = AuthActivityIntent.getStringExtra("email");
        clientConfigurationViewModel.password = AuthActivityIntent.getStringExtra("password");
        clientConfigurationViewModel.nearbyShops = AuthActivityIntent.getParcelableExtra("nearbyShops");
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