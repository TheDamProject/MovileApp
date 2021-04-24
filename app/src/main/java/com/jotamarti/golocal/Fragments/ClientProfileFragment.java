package com.jotamarti.golocal.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jotamarti.golocal.Activities.AuthActivity;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.squareup.picasso.Picasso;

public class ClientProfileFragment extends Fragment {

    private ImageView imageView;
    private TextView txtViewNick;
    private MainActivityViewModel model;
    private Button btnUnsubscribe;
    private Client client;

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
        btnUnsubscribe = view.findViewById(R.id.FreagmentMore_btn_unsubscribe);
        txtViewNick = view.findViewById(R.id.txtViewNick);
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.setTitle(String.valueOf(getText(R.string.ClientProfileFragment_title)));
        client = (Client) model.user;
        txtViewNick.setText(client.getNickName());
        Picasso.get().load(client.getAvatar()).into(imageView);

        btnUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askUserDelete();
            }
        });
    }

    private void askUserDelete() {
        new AlertDialog.Builder(getContext())
                .setMessage("¿Seguro que quiere dar de baja su usuario?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.MainActivity_exitMessage_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Le he dado a que si!!!");
                        Log.d(TAG, "Probando esto: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                        CustomToast.showToast(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), CustomToast.mode.LONGER);
                        //handleBackendErrors();
                        /*model.deleteUserInBackend(client.getUserUid());
                        observeDeletedUserInBackend();*/
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void observeDeletedUserInBackend(){
        model.getDeleteUserInBackendResponse().observe(this, (String response) -> {
            // Hacer esto
            //FirebaseAuth.getInstance().getCurrentUser().delete();
        });
    }

    private void handleBackendErrors(){
        model.getDeleteUserBackendError().observe(this, (BackendErrors error) -> {
            switch (error) {
                case REDIRECTION:
                    CustomToast.showToast(getContext(), getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                case CLIENT_ERROR:
                    CustomToast.showToast(getContext(), getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                case SERVER_ERROR:
                    CustomToast.showToast(getContext(), getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
                default:
                    CustomToast.showToast(getContext(), getString(R.string.error_login_generic), CustomToast.mode.SHORTER);
            }
        });
    }
}