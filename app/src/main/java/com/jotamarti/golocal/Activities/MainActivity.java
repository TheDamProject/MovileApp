package com.jotamarti.golocal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Fragments.MapsFragment;
import com.jotamarti.golocal.Fragments.ClientProfileFragment;
import com.jotamarti.golocal.Fragments.PostsFragment;
import com.jotamarti.golocal.Fragments.ShopProfileFragment;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.SharedPreferences.DataStorage;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.ArrayList;

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
        //manageFragmentSelected();

        Bundle bundle = new Bundle();
        bundle.putString("caller", "MainActivity");
        mainActivityViewModel.postsFragment.setArguments(bundle);
        setCurrentFragment(mainActivityViewModel.postsFragment);

        //String caller = mainActivityViewModel.caller;
        /*if (caller.equals("AuthActivity") || caller.equals("ClientConfigurationActivity") || caller.equals("ShopConfigurationActivity") ||caller.equals("NewPostActivity")) {
            // Probar si podemos quetar esto, aqui teniamos setDataFromIntent.
        }*/
        setDataFromIntent();
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
                    Bundle bundle = new Bundle();
                    bundle.putString("caller", "MainActivity");
                    mainActivityViewModel.postsFragment.setArguments(bundle);
                    setCurrentFragment(mainActivityViewModel.postsFragment);
                    return true;
                } else if (id == R.id.page_maps) {
                    setCurrentFragment(mainActivityViewModel.mapsFragment);
                    return true;
                } else if (id == R.id.page_more) {
                    if (mainActivityViewModel.user instanceof Client) {
                        setCurrentFragment(mainActivityViewModel.clientProfileFragment);
                    } else {
                        // Le pasamos el caller por que el shopProfileFragment lo podemos abrir de distintos sitios
                        Bundle bundle = new Bundle();
                        bundle.putString("caller", "ShopProfile");
                        mainActivityViewModel.shopProfileFragment.setArguments(bundle);
                        setCurrentFragment(mainActivityViewModel.shopProfileFragment);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /*private void manageFragmentSelected() {
        if (mainActivityViewModel.caller.equals("ShopProfile")) {
            setCurrentFragment(mainActivityViewModel.shopProfileFragment);
        } else if (mainActivityViewModel.caller.equals("ShopDetailActivityFromMapRoute")) {
            setCurrentFragment(mainActivityViewModel.mapsFragment);
        } else {
            setCurrentFragment(mainActivityViewModel.postsFragment);
        }

    }*/

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity_fragment_parent, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // We use this when we go back to this activity
        mainActivityViewModel.caller = intent.getStringExtra("caller");
        //manageFragmentSelected();
        if(mainActivityViewModel.caller.equals("NewPostActivity")){
            mainActivityViewModel.intent = intent;
            setDataFromIntent();
        }
        if(mainActivityViewModel.caller.equals("NewPostActivity")){
            CustomToast.showToast(this, "Post created!", CustomToast.mode.SHORTER);
        }
        super.onNewIntent(intent);
    }

    private void setDataFromIntent() {
        //TODO: Arreglar esto cuando tenga el backend
        User user = mainActivityViewModel.intent.getParcelableExtra("user");
        ArrayList<Shop> nearbyShops = mainActivityViewModel.intent.getParcelableArrayListExtra("nearbyShops");

        ArrayList<Post> postList = new ArrayList<>();

        for(int i = 1; i < nearbyShops.size(); i++){
            postList.addAll(nearbyShops.get(i).getShopPosts());
        }

        mainActivityViewModel.user = user;
        mainActivityViewModel.setPosts(postList);
        mainActivityViewModel.setShops(nearbyShops);

        if(mainActivityViewModel.caller.equals("AuthActivity")){
            mainActivityViewModel.userCoordinates = mainActivityViewModel.intent.getParcelableExtra("userCoordinates");
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_forget_logout:
                removeSharedPreferences();
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void removeSharedPreferences(){
        DataStorage dataStorage = new DataStorage(App.getContext());
        dataStorage.removePreferences();
    }
}