package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.jotamarti.golocal.Fragments.ShopProfileFragment;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.ShopDetailActivityViewModel;

public class ShopDetailActivity extends AppCompatActivity {

    private ShopDetailActivityViewModel shopDetailActivityViewModel;

    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.ShopDetailActivity_title));
        initializeViewModel();
        getDataFromIntent();
        initializeFragment();
    }



    private void getDataFromIntent() {
        // Puede estar abierto por MapsFragment y PostDetailActiviy
        Intent intent = getIntent();
        shopDetailActivityViewModel.shop = intent.getParcelableExtra("shop");
        caller = intent.getStringExtra("caller");
        if(caller.equals("PostDetailActivityFromMainActivity")){
            shopDetailActivityViewModel.post = intent.getParcelableExtra("post");
        }
    }

    private void initializeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("caller", caller);
        transaction.add(R.id.activityShop_fragment_parent, ShopProfileFragment.class, bundle);
        transaction.addToBackStack(null);
        transaction.commit();
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

    public void showNextActivity() {
        Intent intent;
        if (caller.equals("MapsFragment")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("caller", "ShopDetailsActivityFromMap");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        } else if(caller.equals("PostDetailActivityFromMainActivity")) { // caller equals PostDetailActivity
            intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("caller", "MainActivity");
            intent.putExtra("shop", shopDetailActivityViewModel.shop);
            intent.putExtra("post", shopDetailActivityViewModel.post);
        } else {
            intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("caller", "ShopDetailsActivityFromPost");
            intent.putExtra("shop", shopDetailActivityViewModel.shop);
            intent.putExtra("post", shopDetailActivityViewModel.post);
        }
        startActivity(intent);
    }

    private void initializeViewModel() {
        shopDetailActivityViewModel = new ViewModelProvider(this).get(ShopDetailActivityViewModel.class);
    }
}