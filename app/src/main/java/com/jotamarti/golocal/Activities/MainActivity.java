package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jotamarti.golocal.Fragments.MapsFragment;
import com.jotamarti.golocal.Fragments.ClientProfileFragment;
import com.jotamarti.golocal.Fragments.PostsFragment;
import com.jotamarti.golocal.Fragments.ShopProfileFragment;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private BottomNavigationView btnNavView;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViewModel();
        initializeActivity();
        manageFragmentSelected();

        String caller = mainActivityViewModel.caller;
        if (caller.equals("AuthActivity") || caller.equals("ClientConfigurationActivity") || caller.equals("ShopConfigurationActivity")) {
            setUserInViewModel();
        }

        manageChangeTitle();
    }

    private void initializeViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }

    private void initializeActivity() {
        // Get data from intent
        mainActivityViewModel.intent = getIntent();
        mainActivityViewModel.caller = mainActivityViewModel.intent.getStringExtra("caller");

        // Initialize views
        btnNavView = findViewById(R.id.MainActivity_bottomNavigationView);

        // Initialize fragments
        mainActivityViewModel.mapsFragment = new MapsFragment();
        mainActivityViewModel.postsFragment = new PostsFragment();
        mainActivityViewModel.clientProfileFragment = new ClientProfileFragment();
        mainActivityViewModel.shopProfileFragment = new ShopProfileFragment();

        // Initialize button listener
        btnNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page_posts) {
                    setCurrentFragment(mainActivityViewModel.postsFragment);
                    return true;
                } else if (id == R.id.page_maps) {
                    setCurrentFragment(mainActivityViewModel.mapsFragment);
                    return true;
                } else if (id == R.id.page_more) {
                    if (mainActivityViewModel.user instanceof Client) {
                        setCurrentFragment(mainActivityViewModel.clientProfileFragment);
                    } else {
                        //TODO : Poner el fragment de la tienda.
                        setCurrentFragment(mainActivityViewModel.shopProfileFragment);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void manageFragmentSelected() {
        if (mainActivityViewModel.caller.equals("ShopProfileFragment")) {
            btnNavView.setSelectedItemId(R.id.page_more);
            setCurrentFragment(mainActivityViewModel.shopProfileFragment);
        } else if (mainActivityViewModel.caller.equals("ShopDetailsActivityFromMap")) {
            setCurrentFragment(mainActivityViewModel.mapsFragment);
        } else {
            setCurrentFragment(mainActivityViewModel.postsFragment);
        }

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity_fragment_parent, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // We use this when we go back to this activity
        mainActivityViewModel.caller = intent.getStringExtra("caller");
        manageFragmentSelected();
        super.onNewIntent(intent);
    }

    private void setUserInViewModel() {
        //TODO: Arreglar esto cuando tenga el backend
        User user = mainActivityViewModel.intent.getParcelableExtra("user");
        List<Shop> nearbyShops = mainActivityViewModel.intent.getParcelableArrayListExtra("nearbyShops");

        Bundle bundle = mainActivityViewModel.intent.getBundleExtra("test");

        List<Shop> test = bundle.getParcelableArrayList("test");

        mainActivityViewModel.user = user;
        mainActivityViewModel.setPosts();
        mainActivityViewModel.setShops();

        mainActivityViewModel.userCoordinates = mainActivityViewModel.intent.getParcelableExtra("userCoordinates");

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.MainActivity_exitMessage))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.MainActivity_exitMessage_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        System.exit(1);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}