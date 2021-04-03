package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.PostDetailActivityViewModel;
import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtViewPostDetail;
    private TextView txtViewPostHeader;
    private ImageView imageViewPostImage;
    private Button btnVisitShop;

    private final String TAG = "PostDetailActivity";

    private PostDetailActivityViewModel postDetailActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeUi();
        Intent intent = getIntent();
        String caller = intent.getStringExtra("caller");
        postDetailActivityViewModel = new ViewModelProvider(this).get(PostDetailActivityViewModel.class);
        if(caller.equals("MainActivity")){
            Post post = (Post) intent.getSerializableExtra("post");
            Shop shop = (Shop) intent.getSerializableExtra("shop");
            postDetailActivityViewModel.setPost(post);
            postDetailActivityViewModel.setShop(shop);

        }
        postDetailActivityViewModel = new ViewModelProvider(this).get(PostDetailActivityViewModel.class);
        Log.d(TAG, "Imprimiendo post: " + postDetailActivityViewModel.getPost().getMessage());
        txtViewPostDetail.setText(postDetailActivityViewModel.getPost().getMessage());
        txtViewPostHeader.setText(postDetailActivityViewModel.getPost().getHeader());
        Picasso.get().load(postDetailActivityViewModel.getPost().getImageUrl().toString()).into(imageViewPostImage);

        btnVisitShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, ShopDetailActivity.class);
                intent.putExtra("shop", postDetailActivityViewModel.getShop());
                intent.putExtra("caller", "PostDetailsActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("caller", "postDetailActivity");
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void initializeUi(){
        txtViewPostDetail = findViewById(R.id.PostDetailsActivity_textView_postHeader);
        txtViewPostHeader = findViewById(R.id.PostDetailsActivity_textView_postDetails);
        imageViewPostImage = findViewById(R.id.PostDetailsActivity_imageView_postImage);
        btnVisitShop = findViewById(R.id.PostDetailActivity_btn_visitShop);
        postDetailActivityViewModel = new ViewModelProvider(PostDetailActivity.this).get(PostDetailActivityViewModel.class);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }
}