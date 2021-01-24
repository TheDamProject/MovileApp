package com.jotamarti.golocal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jotamarti.golocal.R;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtViewPostDetail;
    private TextView txtViewPostHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initializeUi();
        Intent intent = getIntent();
        String texto = intent.getStringExtra("content");

        txtViewPostDetail.setText(texto);
    }

    private void initializeUi(){
        txtViewPostDetail = findViewById(R.id.txtViewPostDetail);
        //txtViewPostHeader = findViewById(R.id.txtViewHeaderCardView);
    }
}