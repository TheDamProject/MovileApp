package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jotamarti.golocal.Fragments.MapsFragment;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btnNavView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private MainActivityViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUi();
        setUserInViewModel();
    }

    private void initializeUi(){
        btnNavView = findViewById(R.id.bottomNavigationView);
        /*btnNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapsFragment:
                        openFragment();
                }
            }
        });*/
        navController = Navigation.findNavController(this,  R.id.fragmentParent);
        NavigationUI.setupWithNavController(btnNavView, navController);
        //appBarConfiguration = new AppBarConfiguration.Builder(R.id.postsFragment, R.id.mapsFragment).build();
        // Con esto aparece el nombre arriba
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    private void setUserInViewModel(){
        Intent intent = getIntent();
        Bitmap avatar = intent.getParcelableExtra("avatar");
        String email = intent.getStringExtra("email");
        String uid = intent.getStringExtra("uid");
        User user = new User(avatar, email, uid);
        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        model.setUser(user);
        model.setPosts();
        /*final Observer<List<Post>> observador = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                Log.d("TXTVIEW", "HOLAAA");
            }
        };*/
        //model.getPosts().observe(MainActivity.this , observador);
    }
}