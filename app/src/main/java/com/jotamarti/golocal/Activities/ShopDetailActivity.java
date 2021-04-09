package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.jotamarti.golocal.Fragments.ShopProfileFragment;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.ViewModels.VisitShopViewModel;

public class ShopDetailActivity extends AppCompatActivity {

    private VisitShopViewModel visitShopViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        initializeUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        visitShopViewModel.setShop((Shop) intent.getParcelableExtra("shop"));

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("caller", "visit");
        bundle.putParcelable("shop", visitShopViewModel.getShop());
        transaction.add(R.id.activityShop_fragment_parent, ShopProfileFragment.class, bundle);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("caller", "ShopDetailsActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void initializeUI(){
        visitShopViewModel = new ViewModelProvider(this).get(VisitShopViewModel.class);
    }
}