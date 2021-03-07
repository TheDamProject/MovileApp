package com.jotamarti.golocal.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jotamarti.golocal.Activities.ShopConfiguration;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.Random;

public class ShopProfileFragment extends Fragment {

    private MainActivityViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.setTitle("Shop Profile");
        View view = inflater.inflate(R.layout.fragment_shop_profile, container, false);

        TextView txtView = view.findViewById(R.id.fragmentShopProfile_txtView_shopDescription);
        Button btnEditProfile = view.findViewById(R.id.fragmentShopProfile_btn_editProfile);
        String texto = "";
        String abecedario = "abcdefghijklmnñopqrstuvwxyz";
        Random random = new Random();
        for(int i = 0; i < 256; i++) {
            int numeroRandom = random.nextInt(abecedario.length());
            texto = texto + abecedario.charAt(numeroRandom);
        }

        txtView.setText(texto);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShopConfiguration.class);
                intent.putExtra("user", model.getUser());
                intent.putExtra("caller", "ShopProfileFragment");
                startActivity(intent);
            }
        });


        return view;
    }
}