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

    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setTitle(R.string.PostDetailActivity_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeUi();
        Intent intent = getIntent();
        caller = intent.getStringExtra("caller");
        postDetailActivityViewModel = new ViewModelProvider(this).get(PostDetailActivityViewModel.class);
        if(caller.equals("MainActivity")){
            Post post = intent.getParcelableExtra("post");
            Shop shop = intent.getParcelableExtra("shop");
            postDetailActivityViewModel.setPost(post);
            postDetailActivityViewModel.setShop(shop);

            btnVisitShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostDetailActivity.this, ShopDetailActivity.class);
                    intent.putExtra("shop", postDetailActivityViewModel.getShop());
                    intent.putExtra("caller", "PostDetailActivityFromMainActivity");
                    intent.putExtra("post", postDetailActivityViewModel.getPost());
                    startActivity(intent);
                }
            });

        } else if(caller.equals("MapsFragment")) {
            Post post = intent.getParcelableExtra("post");
            Shop shop = intent.getParcelableExtra("shop");
            postDetailActivityViewModel.setPost(post);
            postDetailActivityViewModel.setShop(shop);
            btnVisitShop.setVisibility(View.INVISIBLE);
        } else {
            Post post = intent.getParcelableExtra("post");
            postDetailActivityViewModel.setPost(post);
            btnVisitShop.setVisibility(View.INVISIBLE);
        }
        txtViewPostDetail.setText(postDetailActivityViewModel.getPost().getMessage());
        txtViewPostHeader.setText(postDetailActivityViewModel.getPost().getHeader());
        Picasso.get().load(postDetailActivityViewModel.getPost().getImageUrl()).into(imageViewPostImage);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        showNextActivity();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showNextActivity();
    }

    public void showNextActivity(){
        if (caller.equals("MainActivity")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("caller", "postDetailActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ShopDetailActivity.class);
            intent.putExtra("caller", "MapsFragment");
            intent.putExtra("shop", postDetailActivityViewModel.getShop());
            startActivity(intent);
        }
    }

    private void initializeUi(){
        txtViewPostDetail = findViewById(R.id.PostDetailsActivity_textView_postHeader);
        txtViewPostHeader = findViewById(R.id.PostDetailsActivity_textView_postDetails);
        imageViewPostImage = findViewById(R.id.PostDetailsActivity_imageView_postImage);
        btnVisitShop = findViewById(R.id.PostDetailActivity_btn_visitShop);
        postDetailActivityViewModel = new ViewModelProvider(PostDetailActivity.this).get(PostDetailActivityViewModel.class);
    }
}