package com.jotamarti.golocal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.R;
import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtViewPostDetail;
    private TextView txtViewPostHeader;
    private ImageView imageViewPostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initializeUi();
        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");

        txtViewPostDetail.setText(post.getMessage());
        txtViewPostHeader.setText(post.getHeader());
        Picasso.get().load(post.getImage().toString()).into(imageViewPostImage);
    }

    private void initializeUi(){
        txtViewPostDetail = findViewById(R.id.PostDetailsActivity_textView_postHeader);
        txtViewPostHeader = findViewById(R.id.PostDetailsActivity_textView_postDetails);
        imageViewPostImage = findViewById(R.id.PostDetailsActivity_imageView_postImage);
    }
}