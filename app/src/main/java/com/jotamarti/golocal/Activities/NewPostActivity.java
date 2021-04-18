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
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.ImageUtil;
import com.jotamarti.golocal.ViewModels.NewPostActivityViewModel;
import com.jotamarti.golocal.ViewModels.ShopConfigurationViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewPostActivity extends AppCompatActivity {

    private final static int PERMISSIONS_REQUEST_CAMERA = 1;
    private final String TAG = "NewPostActivity";

    private Button btnChangeImage;
    private Button btnSavePost;
    private EditText editTextTitle;
    private EditText editTextContent;
    private ImageView imageViewPostImage;
    private TextWatcher textWatcher;
    private CircularProgressIndicator spinnerLoading;

    private NewPostActivityViewModel newPostActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        initializeTextWatcher();
        initializeViewModel();
        initializeUi();
        getDataFromMainActivity();
        manageBackendErrors();
        newPostActivityViewModel.post = new Post();

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveCameraPermissions()) {
                    CropImage.startPickImageActivity(NewPostActivity.this);
                } else {
                    askCameraPermissions();
                }

            }
        });

        btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingState(true);
                newPostActivityViewModel.post.setHeader(editTextTitle.getText().toString());
                newPostActivityViewModel.post.setMessage(editTextContent.getText().toString());
                newPostActivityViewModel.post.setCompanyUid(newPostActivityViewModel.thisShop.getUserUid());
                newPostActivityViewModel.createPostInBackend();
                observePostFromBackend();
            }
        });
    }

    public void loadingState(Boolean loadingState){
        if(loadingState){
            spinnerLoading.setVisibility(View.VISIBLE);
            btnSavePost.setEnabled(false);
        } else {
            spinnerLoading.setVisibility(View.INVISIBLE);
            btnSavePost.setEnabled(true);
        }
    }

    public void observePostFromBackend(){
        newPostActivityViewModel.getBackendPost().observe(this, (Post post) -> {
            newPostActivityViewModel.post = post;
            updateShop();
            showMainActivity();
            newPostActivityViewModel.getBackendPost().removeObservers(this);
        });
    }

    private void updateShop() {
        for(int i = 0; i < newPostActivityViewModel.shopList.size(); i++){
            Shop shop = newPostActivityViewModel.shopList.get(i);
            Post post = newPostActivityViewModel.post;
            if(shop.getUserUid().equals(post.getCompanyUid())){
                shop.getShopPosts().add(post);
                newPostActivityViewModel.thisShop = shop;
                break;
            }
        }
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("caller", "NewPostActivity");
        intent.putParcelableArrayListExtra("nearbyShops", newPostActivityViewModel.shopList);
        intent.putExtra("user", newPostActivityViewModel.thisShop);
        startActivity(intent);
    }

    private void getDataFromMainActivity() {
        Intent MainActivityIntent = getIntent();
        newPostActivityViewModel.thisShop = MainActivityIntent.getParcelableExtra("user");
        newPostActivityViewModel.shopList = MainActivityIntent.getParcelableArrayListExtra("shopList");
    }

    private void initializeUi(){
        btnChangeImage = findViewById(R.id.NewPostActivity_btn_changeShopProfileImage);
        btnSavePost = findViewById(R.id.NewPostActivity_btn_save);
        editTextTitle = findViewById(R.id.NewPostActivity_editText_title);
        editTextContent = findViewById(R.id.NewPostActivity_editText_content);
        imageViewPostImage = findViewById(R.id.NewPostActivity_imageView_postImage);
        spinnerLoading = findViewById(R.id.NewPostActivity_spinner_loading);
        loadingState(false);

        editTextContent.addTextChangedListener(textWatcher);
        editTextTitle.addTextChangedListener(textWatcher);

        btnSavePost.setEnabled(false);

        setTitle(R.string.NewPostActivity_title);
    }

    private void initializeViewModel() {
        newPostActivityViewModel = new ViewModelProvider(this).get(NewPostActivityViewModel.class);
    }

    private void checkAllDataInserted() {
        if(editTextTitle.getText().toString().length() > 0 && editTextContent.getText().toString().length() > 0 && newPostActivityViewModel.imageInserted){
            btnSavePost.setEnabled(true);
        } else {
            btnSavePost.setEnabled(false);
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
                newPostActivityViewModel.imageBase64 = ImageUtil.UriToBase64(uri, ImageUtil.IMAGE_TYPE.SHOP);
                newPostActivityViewModel.post.setImageUrl(newPostActivityViewModel.imageBase64);
                imageViewPostImage.setImageURI(uri);
                newPostActivityViewModel.imageInserted = true;
                checkAllDataInserted();
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
                CropImage.startPickImageActivity(NewPostActivity.this);
            } else {
                CustomToast.showToast(this, "Fail to get permission", CustomToast.mode.LONGER);
            }
        }
    }

    private void manageBackendErrors() {
        newPostActivityViewModel.getBackendError().observe(this, (BackendErrors error) -> {
            switch (error) {
                case REDIRECTION:
                    CustomToast.showToast(NewPostActivity.this, getString(R.string.error_failed_create_post), CustomToast.mode.SHORTER);
                case CLIENT_ERROR:
                    CustomToast.showToast(NewPostActivity.this, getString(R.string.error_failed_create_post), CustomToast.mode.SHORTER);
                case SERVER_ERROR:
                    CustomToast.showToast(NewPostActivity.this, getString(R.string.error_failed_create_post), CustomToast.mode.SHORTER);
                default:
                    CustomToast.showToast(NewPostActivity.this, getString(R.string.error_failed_create_post), CustomToast.mode.SHORTER);
            }
        });
        loadingState(false);
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