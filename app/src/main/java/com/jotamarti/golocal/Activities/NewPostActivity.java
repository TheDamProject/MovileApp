package com.jotamarti.golocal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jotamarti.golocal.R;

public class NewPostActivity extends AppCompatActivity {

    private Button btnChangeImage;
    private Button btnSavePost;
    private EditText editTextTitle;
    private EditText editTextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
    }

    private void initializeUi(){
        btnChangeImage = findViewById(R.id.NewPostActivity_btn_changeShopProfileImage);
        btnSavePost = findViewById(R.id.NewPostActivity_btn_save);
        editTextTitle = findViewById(R.id.NewPostActivity_editText_title);
        editTextContent = findViewById(R.id.NewPostActivity_editText_content);
    }
}