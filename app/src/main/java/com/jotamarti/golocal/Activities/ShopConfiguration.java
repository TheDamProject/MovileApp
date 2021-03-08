package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class ShopConfiguration extends AppCompatActivity {

    private final String TAG = "ShopConfiguration";
    private final static int PERMISSIONS_REQUEST_CAMERA = 1;
    private final static int CAMERA_REQ_CODE = 2;
    private EditText editTextPhone;
    private TextInputEditText textInputShopDescription;
    private TextView txtViewNumber;
    private CheckBox checkBoxNoNumber;
    private Button btnUploadImage;
    private Button btnSave;
    private ImageView imageViewShopHeader;
    private AutocompleteSupportFragment autocompleteFragment;
    private Shop shop;
    private TextWatcher textWatcher;
    private String directionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_configuration);
        initializeTextWatcher();
        initializeUi();

        Intent previousIntent = getIntent();
        shop = previousIntent.getParcelableExtra("user");


        initializeAutoCompleteFragment();



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                directionName = place.getName();
                if (!directionHasNumber(directionName)) {
                    // TODO: tendremos que manejar cuando el usuario mete
                    showInputNumber();
                } else {
                    hideInputNumber();
                }
                checkAllDataInserted();
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        checkBoxNoNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomToast.showToast(ShopConfiguration.this, "Please, if your address have number consider inserting it", CustomToast.mode.LONGER);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveCameraPermissions()) {
                    CropImage.startPickImageActivity(ShopConfiguration.this);
                } else {
                    askCameraPermissions();
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caller = previousIntent.getStringExtra("caller");

                if (caller.equals("ShopProfileFragment")) {
                    // TODO: Cuando le de a guardar desde ShopProfileFragment tendre que actualizar en el bancked, despues actualizar el objeto y volver al perfil
                    Shop currentShop = (Shop) previousIntent.getParcelableExtra("user");
                    Intent intent = new Intent(ShopConfiguration.this, MainActivity.class);
                    intent.putExtra("user", currentShop);
                    intent.putExtra("caller", "ShopProfileFragment");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ShopConfiguration.this, MainActivity.class);
                    Shop shop = new Shop();
                    intent.putExtra("user", shop);
                    intent.putExtra("caller", "ShopConfiguration");
                    startActivity(intent);
                }
            }
        });
    }

    private void checkAllDataInserted() {
        View autoCompleteFragmentView = autocompleteFragment.getView();
        EditText etTextInput = autoCompleteFragmentView.findViewById(R.id.places_autocomplete_search_input);
        autoCompleteFragmentView.findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTextInput.setText("");
                directionName = "";
                btnSave.setEnabled(false);
            }
        });
        Log.d(TAG, "Texto: " + directionName);
        if (editTextPhone.getText().toString().length() > 0 && textInputShopDescription.getText().toString().length() > 0 && directionName.length() > 0) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
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
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            strartCrop(imageuri);
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
                imageViewShopHeader.setImageURI(result.getUri());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void strartCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(2, 1).start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            Log.d(TAG, "Lenght of grantResult" + String.valueOf(grantResults.length));
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(ShopConfiguration.this);
            } else {
                CustomToast.showToast(this, "Fail to get permission", CustomToast.mode.LONGER);
            }
        }
    }

    private Boolean directionHasNumber(String directionName) {
        return directionName.contains(",");
    }

    private void initializeUi() {
        // Iniciamos vistas
        txtViewNumber = findViewById(R.id.shopConfigTxtViewNumber);
        checkBoxNoNumber = findViewById(R.id.ShopConfiguration_checkbox_noNumber);
        btnUploadImage = findViewById(R.id.fragmentShopProfile_btn_changeShopProfileImage);
        imageViewShopHeader = findViewById(R.id.fragmentShopProfile_imageView_shopProfileImage);
        btnSave = findViewById(R.id.activityShopConfiguration_btn_save);
        editTextPhone = findViewById(R.id.ShopConfiguration_editText_phone);
        textInputShopDescription = findViewById(R.id.ShopConfiguration_textField_shopDescription);


        txtViewNumber.setVisibility(View.INVISIBLE);
        checkBoxNoNumber.setVisibility(View.INVISIBLE);

        btnSave.setEnabled(false);
        Places.initialize(getApplicationContext(), getString(R.string.api_key));

        // Listeners para activar el botón de guardado
        textInputShopDescription.addTextChangedListener(textWatcher);
        editTextPhone.addTextChangedListener(textWatcher);
    }

    private void initializeAutoCompleteFragment() {
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setCountries("ES");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.ADDRESS, Place.Field.LAT_LNG));
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

    private void showInputNumber() {
        txtViewNumber.setVisibility(View.VISIBLE);
        checkBoxNoNumber.setVisibility(View.VISIBLE);
    }

    private void hideInputNumber() {
        txtViewNumber.setVisibility(View.INVISIBLE);
        checkBoxNoNumber.setVisibility(View.INVISIBLE);
    }
}