package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jotamarti.golocal.Fragments.MapsFragment;
import com.jotamarti.golocal.Fragments.ClientProfileFragment;
import com.jotamarti.golocal.Fragments.PostsFragment;
import com.jotamarti.golocal.Fragments.ShopProfileFragment;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private BottomNavigationView btnNavView;
    private MainActivityViewModel mainActivityViewModel;
    private Intent intent;
    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        caller = intent.getStringExtra("caller");

        initializeUi();
        manageFragmentSelected();
        if(caller.equals("AuthActivity") || caller.equals("ClientConfigurationActivity")) {
            setUserInViewModel();
        }
        manageChangeTitle();

    }

    private void initializeUi() {
        btnNavView = findViewById(R.id.MainActivity_bottomNavigationView);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }

    private void manageFragmentSelected() {
        Fragment mapsFragment = new MapsFragment();
        Fragment postsFragment = new PostsFragment();
        Fragment clientProfileFragment = new ClientProfileFragment();
        Fragment shopProfileFragment = new ShopProfileFragment();

        if (caller.equals("ShopProfileFragment")) {
            btnNavView.setSelectedItemId(R.id.page_more);
            setCurrentFragment(shopProfileFragment);
        } else {
            setCurrentFragment(postsFragment);
        }

        btnNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page_posts) {
                    setCurrentFragment(postsFragment);
                    return true;
                } else if (id == R.id.page_maps) {
                    setCurrentFragment(mapsFragment);
                    return true;
                } else if (id == R.id.page_more) {
                    if (mainActivityViewModel.getUser() instanceof Client) {
                        setCurrentFragment(clientProfileFragment);
                    } else {
                        //TODO : Poner el fragment de la tienda.
                        setCurrentFragment(shopProfileFragment);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }



    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity_fragment_parent, fragment).commit();
    }

    private void setUserInViewModel() {
        User user = intent.getParcelableExtra("user");
        List<Shop> nearbyShops = intent.getParcelableArrayListExtra("nearbyShops");

        mainActivityViewModel.setUser(user);
        mainActivityViewModel.setPosts();
        mainActivityViewModel.setShops();

        /*final Observer<List<Post>> observador = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                Log.d("TXTVIEW", "HOLAAA");
            }
        };*/
        //model.getPosts().observe(MainActivity.this , observador);
    }

    private void manageChangeTitle() {
        mainActivityViewModel.getTitle().observe(this, (String title) -> {
            setTitle(title);
        });
    }
}