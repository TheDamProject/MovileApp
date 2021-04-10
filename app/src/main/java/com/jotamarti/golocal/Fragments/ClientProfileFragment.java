package com.jotamarti.golocal.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.squareup.picasso.Picasso;

public class ClientProfileFragment extends Fragment {

    private ImageView imageView;
    private TextView txtViewEmail;
    private MainActivityViewModel model;

    private final String TAG = "MORE_FRAGMENT";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.ClientConfigurationActivity_imageView_userAvatar);
        txtViewEmail = view.findViewById(R.id.txtViewEmail);
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.setTitle("Client Profile");
        User user = model.user;
        Picasso.get().load(user.getAvatar().toString()).into(imageView);

        //TODO: Eliminar tema email
        //txtViewEmail.setText(user.getEmail());
    }
}