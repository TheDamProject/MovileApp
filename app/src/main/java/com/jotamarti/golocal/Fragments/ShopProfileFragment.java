package com.jotamarti.golocal.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.ViewModels.ShopDetailActivityViewModel;

import java.util.Random;

public class ShopProfileFragment extends Fragment {

    private MainActivityViewModel mainActivityViewModel;
    private ShopDetailActivityViewModel shopDetailActivityViewModel;

    private String TAG = "ShopProfileFragment";

    private Button btnEditProfile;
    private TextView textViewShopDescription;
    private TextView textViewShopLocation;
    private TextView textViewShopPhoneNumber;

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

        if(caller.equals("MapsFragment")) {
            initializeShopDetailViewModel();
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("caller", caller);
            transaction.add(R.id.fragmentShopProfile_fragmentParent_shopsPosts, PostsFragment.class, bundle);
            transaction.addToBackStack(null);
            transaction.commit();
            shop = shopDetailActivityViewModel.shop;
        } else if(caller.equals("PostDetailActivityFromMainActivity")){
            initializeShopDetailViewModel();
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("caller", "PostDetailActivityFromMainActivity");
            transaction.add(R.id.fragmentShopProfile_fragmentParent_shopsPosts, PostsFragment.class, bundle);
            transaction.addToBackStack(null);
            transaction.commit();
            shop = shopDetailActivityViewModel.shop;
        } else {
            mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            mainActivityViewModel.setTitle("Shop Profile");
            shop = (Shop) mainActivityViewModel.user;
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(getContext(), ShopConfigurationActivity.class);
                    //intent.putExtra("user", model.getUser());
                    //intent.putExtra("caller", "ShopProfileFragment");
                    //startActivity(intent);
                }
            });
        }



        String texto = "";
        String abecedario = "abcdefghijklmnñopqrstuvwxyz";
        Random random = new Random();
        for(int i = 0; i < 256; i++) {
            int numeroRandom = random.nextInt(abecedario.length());
            texto = texto + abecedario.charAt(numeroRandom);
        }

        textViewShopDescription.setText(shop.getDescription());
        textViewShopLocation.setText(shop.getAddress());
        textViewShopPhoneNumber.setText(shop.getTelNumber());

        return view;
    }

    private void initializeShopDetailViewModel() {
        shopDetailActivityViewModel = new ViewModelProvider(requireActivity()).get(ShopDetailActivityViewModel.class);
    }

    private void initiaizeUi(String mode, View view){
        textViewShopDescription = view.findViewById(R.id.FragmentShopProfile_textView_shopDescription);
        textViewShopLocation = view.findViewById(R.id.FragmentShopProfile_textView_Location);
        textViewShopPhoneNumber = view.findViewById(R.id.FragmentShopProfile_textView_PhoneNumber);
        btnEditProfile = view.findViewById(R.id.fragmentShopProfile_btn_editProfile);
        if(mode.equals("visit") || mode.equals("PostDetailActivityFromMainActivity") || mode.equals("MapsFragment")){
            btnEditProfile.setVisibility(View.INVISIBLE);
        } else {
            btnEditProfile.setVisibility(View.VISIBLE);
        }
    }
}