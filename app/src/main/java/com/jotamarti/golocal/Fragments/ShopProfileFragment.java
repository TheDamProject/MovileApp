package com.jotamarti.golocal.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jotamarti.golocal.Activities.NewPostActivity;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.ViewModels.ShopDetailActivityViewModel;

import java.util.ArrayList;
import java.util.Random;

public class ShopProfileFragment extends Fragment {

    private MainActivityViewModel mainActivityViewModel;
    private ShopDetailActivityViewModel shopDetailActivityViewModel;

    private String TAG = "ShopProfileFragment";

    private Button btnEditProfile;
    private Button btnCreatePost;
    private TextView textViewShopDescription;
    private TextView textViewShopLocation;
    private TextView textViewShopPhoneNumber;
    private TextView textViewShopName;

    // Lo pongo aqui por que esta fragment puede tener diferentes viewmodels
    private Shop shop;
    private String caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_profile, container, false);
        Bundle previousBundle = this.getArguments();

        caller = previousBundle.getString("caller");

        initiaizeUi(caller, view);

        if (caller.equals("MapsFragment") || caller.equals("PostDetailActivityFromMainActivity")) {
            initializeShopDetailViewModel();
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("caller", caller);
            transaction.add(R.id.fragmentShopProfile_fragmentParent_shopsPosts, PostsFragment.class, bundle);
            transaction.addToBackStack(null);
            transaction.commit();
            shop = shopDetailActivityViewModel.shop;
        } else {
            // Tienda visita su perfil
            mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            mainActivityViewModel.setTitle("Shop Profile");
            shop = (Shop) mainActivityViewModel.user;


            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("caller", caller); // El caller es ShopProfile
            transaction.add(R.id.fragmentShopProfile_fragmentParent_shopsPosts, PostsFragment.class, bundle);
            transaction.addToBackStack(null);
            transaction.commit();

            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(getContext(), ShopConfigurationActivity.class);
                    //intent.putExtra("user", model.getUser());
                    //intent.putExtra("caller", "ShopProfileFragment");
                    //startActivity(intent);
                }
            });

            btnCreatePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NewPostActivity.class);
                    intent.putExtra("user", (Shop) mainActivityViewModel.user);
                    intent.putParcelableArrayListExtra("shopList", (ArrayList<? extends Parcelable>) mainActivityViewModel.getShopsList().getValue());
                    startActivity(intent);
                }
            });
        }

        textViewShopDescription.setText(shop.getDescription());
        textViewShopLocation.setText(shop.getAddress());
        textViewShopPhoneNumber.setText(shop.getTelNumber());
        textViewShopName.setText(shop.getShopName());

        return view;
    }

    private void initializeShopDetailViewModel() {
        shopDetailActivityViewModel = new ViewModelProvider(requireActivity()).get(ShopDetailActivityViewModel.class);
    }

    private void initiaizeUi(String mode, View view) {
        textViewShopDescription = view.findViewById(R.id.FragmentShopProfile_textView_shopDescription);
        textViewShopLocation = view.findViewById(R.id.FragmentShopProfile_textView_Location);
        textViewShopPhoneNumber = view.findViewById(R.id.FragmentShopProfile_textView_PhoneNumber);
        btnEditProfile = view.findViewById(R.id.fragmentShopProfile_btn_editProfile);
        btnCreatePost = view.findViewById(R.id.fragmentShopProfile_btn_addPost);
        textViewShopName = view.findViewById(R.id.fragmentShopProfile_textView_shopNameText);
        if (mode.equals("visit") || mode.equals("PostDetailActivityFromMainActivity") || mode.equals("MapsFragment")) {
            btnEditProfile.setVisibility(View.INVISIBLE);
        } else {
            btnEditProfile.setVisibility(View.VISIBLE);
            btnCreatePost.setVisibility(View.VISIBLE);
        }
    }
}