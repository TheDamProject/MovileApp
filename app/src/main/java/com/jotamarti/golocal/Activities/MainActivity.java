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
import com.jotamarti.golocal.Fragments.MoreFragment;
import com.jotamarti.golocal.Fragments.PostsFragment;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private BottomNavigationView btnNavView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private MainActivityViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserInViewModel();
        initializeUi();


    }

    private void initializeUi(){
        btnNavView = findViewById(R.id.bottomNavigationView);

        Fragment mapsFragment = new MapsFragment();
        Fragment postsFragment = new PostsFragment();
        Fragment moreFragment = new MoreFragment();

        setCurrentFragment(postsFragment);


        btnNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.page_posts) {
                    setCurrentFragment(postsFragment);
                    return true;
                } else if (id == R.id.page_maps) {
                    setCurrentFragment(mapsFragment);
                    return true;
                } else if(id == R.id.page_more) {
                    setCurrentFragment(moreFragment);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setCurrentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentParent, fragment).commit();
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
        model.getTitle().observe(this, (String title) -> {
            Log.d(TAG, "Nuevo titulo recibido " + title);
           setTitle(title);
        });
        /*final Observer<List<Post>> observador = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                Log.d("TXTVIEW", "HOLAAA");
            }
        };*/
        //model.getPosts().observe(MainActivity.this , observador);
    }
}