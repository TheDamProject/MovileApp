package com.jotamarti.golocal.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;
import com.jotamarti.golocal.ViewModels.ShopDetailActivityViewModel;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends Fragment {

    private MainActivityViewModel mainActivityViewModel;
    private ShopDetailActivityViewModel shopDetailActivityViewModel;
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
        getBundle();

        List<Post> postList;
        List<Shop> nearbyShops;
        if (caller.equals("MapsFragment") || caller.equals("PostDetailActivityFromMainActivity")) {
            initializeShopDetailViewModel();
            shop = shopDetailActivityViewModel.shop;
            postList = shop.getShopPosts();
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, caller);
                adapter.setShop(shop);
                recyclerView.setAdapter(adapter);
            }
        } else if(caller.equals("ShopProfile")) {
            initializeMainActivityViewModel();
            shop = (Shop) mainActivityViewModel.user;
            postList = shop.getShopPosts();
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, caller);
                adapter.setShop(shop);
                mainActivityViewModel.getPosts().observe(requireActivity(), (List<Post> newPostList) -> {
                    ArrayList<Post> newArrayPostList = new ArrayList<>(newPostList);
                    adapter.setPostsList(newArrayPostList);
                });
                recyclerView.setAdapter(adapter);
            }
        } else {
            // Caller equals MainActivity
            initializeMainActivityViewModel();
            postList = mainActivityViewModel.getPosts().getValue();
            nearbyShops = mainActivityViewModel.getShopsList().getValue();
            mainActivityViewModel.setTitle("Posts List");
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, "MainActivity");
                adapter.setShopList(nearbyShops);
                mainActivityViewModel.getPosts().observe(requireActivity(), (List<Post> newPostList) -> {
                    ArrayList<Post> newArrayPostList = new ArrayList<>(newPostList);
                    adapter.setPostsList(newArrayPostList);
                });
                recyclerView.setAdapter(adapter);
            }
        }


        return view;
    }

    private void getBundle() {
        // El PostFragment puede venir de MainActivity o de ShopDetail
        // Si viene de MainActivity tenemos el viewmodel para sacar la informacion
        // Pero si viene de shopDetail tenemos que recuperar la información de alguna manera.
        Bundle previousBundle = this.getArguments();
        if (previousBundle != null) {
            caller = previousBundle.getString("caller");
        } else {
            caller = "MainActivity";
        }
    }

    private void initializeShopDetailViewModel() {
        shopDetailActivityViewModel = new ViewModelProvider(requireActivity()).get(ShopDetailActivityViewModel.class);
    }

    private void initializeMainActivityViewModel(){
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
    }
}