package com.jotamarti.golocal.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jotamarti.golocal.Adapters.PostsRecyclerViewAdapter;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.List;


public class PostsFragment extends Fragment {

    private MainActivityViewModel model;
    private final String TAG = "PostsFragment";
    private String caller;
    private Shop shop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_item_list, container, false);

        Bundle previousBundle = this.getArguments();

        if(previousBundle != null){
            caller = previousBundle.getString("caller");
            shop = (Shop) previousBundle.getSerializable("shop");
        } else {
            caller = "other";
        }



        if (caller.equals("visit")) {
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(shop.getShopPosts(), context);
                recyclerView.setAdapter(adapter);
            }
        } else {
            model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            List<Post> posts = model.getPosts().getValue();
            model.setTitle("Posts List");
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(posts, context);
                model.getShopsList().observe(requireActivity(), (List<Shop> shopList) -> {
                    adapter.setShopList(shopList);
                    Log.d(TAG, "He puesto la lista de posts");
                });
                recyclerView.setAdapter(adapter);
            }
        }


        return view;
    }
}