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
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jotamarti.golocal.Utils.ImageUtil;
import com.jotamarti.golocal.ViewModels.ShopConfigurationViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ShopConfigurationActivity extends AppCompatActivity {

    private final String TAG = "ShopConfiguration";
    private final static int PERMISSIONS_REQUEST_CAMERA = 1;
    private final static int CAMERA_REQ_CODE = 2;

    // Views
    private EditText editTextPhone;
    private EditText editTextName;
    private TextInputEditText textInputShopDescription;
    private TextView txtViewNumber;
    private CheckBox checkBoxNoNumber;
    private CheckBox isWhatsapp;
    private Button btnUploadImage;
    private Button btnSave;
    private ImageView imageViewShopHeader;

    // Views from the autoCompleteFragment
    View autoCompleteFragmentView;
    EditText autoCompleteFragmentEditText;

    private ShopConfigurationViewModel shopConfigurationViewModel;

    private AutocompleteSupportFragment autocompleteFragment;

    private TextWatcher textWatcher;
    private String directionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_configuration);

        initializeViewModel();
        getDataFromAuthActivity();
        initializeTextWatcher();
        initializeUi();
        initializeAutoCompleteFragment();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                shopConfigurationViewModel.shop.setCoordinates(place.getLatLng());
                Log.d(TAG, directionName = place.getName());
                directionName = place.getName();
                if (!directionHasNumber(directionName)) {
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
                CustomToast.showToast(ShopConfigurationActivity.this, "Please, if your address have number consider inserting it", CustomToast.mode.LONGER);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveCameraPermissions()) {
                    CropImage.startPickImageActivity(ShopConfigurationActivity.this);
                } else {
                    askCameraPermissions();
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shopConfigurationViewModel.shop.setDescription(textInputShopDescription.getText().toString());
                shopConfigurationViewModel.shop.setTelNumber(editTextPhone.getText().toString());
                shopConfigurationViewModel.shop.setWhatsapp(isWhatsapp.isChecked());

                //TODO: Llamar al backend y actualizar la tienda

                if (shopConfigurationViewModel.caller.equals("ShopProfileFragment")) {
                    // TODO: Cuando le de a guardar desde ShopProfileFragment tendre que actualizar en el bancked, despues actualizar el objeto y volver al perfil
                    Intent intent = new Intent(ShopConfigurationActivity.this, MainActivity.class);
                    intent.putExtra("user", shopConfigurationViewModel.shop);
                    intent.putExtra("caller", "ShopProfileFragment");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ShopConfigurationActivity.this, MainActivity.class);
                    intent.putExtra("user", shopConfigurationViewModel.shop);
                    intent.putExtra("caller", "ShopConfiguration");
                    startActivity(intent);
                }
            }
        });
    }

    private void checkAllDataInserted() {
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
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            startCrop(imageUri);
        }
        // Aqui entra cuando le damos aceptar en la pantalla de crop
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                shopConfigurationViewModel.imageBase64 = ImageUtil.UriToBase64(uri);
                imageViewShopHeader.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(2, 1).start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(ShopConfigurationActivity.this);
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
        txtViewNumber = findViewById(R.id.textView12);
        checkBoxNoNumber = findViewById(R.id.ShopConfigurationActivity_checkbox_noNumber);
        btnUploadImage = findViewById(R.id.ShopConfigurationActivity_btn_changeShopProfileImage);
        imageViewShopHeader = findViewById(R.id.ShopConfigurationActivity_imageView_shopProfileImage);
        btnSave = findViewById(R.id.ShopConfigurationActivity_btn_save);
        editTextPhone = findViewById(R.id.ShopConfigurationActivity_editText_phone);
        textInputShopDescription = findViewById(R.id.ShopConfigurationActivity_textField_shopDescription);
        isWhatsapp = findViewById(R.id.ShopConfigurationActivity_checkBox_isWhatsapp);
        editTextName = findViewById(R.id.ShopConfigurationActivity_editText_shopName);


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

        // Sacamos la vista del autoCompleteFragment
        autoCompleteFragmentView = autocompleteFragment.getView();
        autoCompleteFragmentEditText = autoCompleteFragmentView.findViewById(R.id.places_autocomplete_search_input);

        // Ponemos un listener en la X para cuando el usuario clique en el, vaciamos el texto, vaciamos la variable directionName y deshabilitamos el botón de salvado.
        autoCompleteFragmentView.findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteFragmentEditText.setText("");
                directionName = "";
                btnSave.setEnabled(false);
            }
        });
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

    private void initializeViewModel(){
        shopConfigurationViewModel = new ViewModelProvider(this).get(ShopConfigurationViewModel.class);
        manageBackendErrors();
        manageAuthServiceErrors();
    }

    private void manageAuthServiceErrors() {
    }

    private void manageBackendErrors() {
    }

    private void getDataFromAuthActivity() {
        Intent AuthActivityIntent = getIntent();
        shopConfigurationViewModel.caller = AuthActivityIntent.getStringExtra("caller");
        if(shopConfigurationViewModel.caller.equals("AuthActivity")){
            shopConfigurationViewModel.email = AuthActivityIntent.getStringExtra("email");
            shopConfigurationViewModel.password = AuthActivityIntent.getStringExtra("password");
            shopConfigurationViewModel.nearbyShops = AuthActivityIntent.getParcelableArrayListExtra("nearbyShops");
            shopConfigurationViewModel.shop = new Shop();
        } else {
            shopConfigurationViewModel.shop = AuthActivityIntent.getParcelableExtra("user");
        }

    }
}