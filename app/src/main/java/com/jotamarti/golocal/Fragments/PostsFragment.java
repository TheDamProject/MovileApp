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
        if (caller.equals("MapsFragment")) {
            initializeShopDetailViewModel();
            shop = shopDetailActivityViewModel.shop;
            postList = shop.getShopPosts();
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, caller);
                adapter.setShop(shop);
                recyclerView.setAdapter(adapter);
            }
        } else if (caller.equals("PostDetailActivityFromMainActivity")) {
            initializeShopDetailViewModel();
            shop = shopDetailActivityViewModel.shop;
            postList = shop.getShopPosts();
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, "PostDetailActivityFromMainActivity");
                adapter.setShop(shop);
                recyclerView.setAdapter(adapter);
            }
        } else {
            mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            postList = mainActivityViewModel.getPosts().getValue();
            mainActivityViewModel.setTitle("Posts List");
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(postList, context, "MainActivity");
                mainActivityViewModel.getShopsList().observe(requireActivity(), (List<Shop> shopList) -> {
                    adapter.setShopList(shopList);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "He puesto la lista de posts");
                });
                recyclerView.setAdapter(adapter);
            }
        }


        return view;
    }

    private void getBundle() {
        // El PostFragment puede venir de MainActivity o de ShopDetail
        // Si viene de MainActivity tenemos el viewmodel para salara la informacion
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
}