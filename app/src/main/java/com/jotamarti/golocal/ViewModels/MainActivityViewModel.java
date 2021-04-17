package com.jotamarti.golocal.ViewModels;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.dummy.PostsDummy;
import com.jotamarti.golocal.dummy.ShopsDummy;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public User user;
    public LatLng userCoordinates;
    public Intent intent;
    public String caller;
    public Fragment mapsFragment;
    public Fragment postsFragment;
    public Fragment clientProfileFragment;
    public Fragment shopProfileFragment;

    private ArrayList<Shop> nearbyShops;
    private ArrayList<Post> allShopsPostList;


    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<List<Shop>> shopsList = new MutableLiveData<>();

    //TODO: Poner lista de posts
    public void setPosts(ArrayList<Post> postList) {
        this.posts.setValue(postList);
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }

    public LiveData<List<Shop>> getShopsList() {
        return shopsList;
    }

    public void setShops(ArrayList<Shop> shopList) {
        shopsList.setValue(shopList);
    }

    public List<Shop> getNearbyShops(){
        return nearbyShops;
    }

    public void setNearbyShops(ArrayList<Shop> nearbyShops){
        this.nearbyShops = nearbyShops;
    }

    public void setAllShopsPostList(ArrayList<Shop> nearbyShops) {
        ArrayList<Post> postList = new ArrayList<>();

        for(int i = 1; i < nearbyShops.size(); i++){
            postList.addAll(nearbyShops.get(i).getShopPosts());
        }
        this.allShopsPostList = postList;
    }

    public ArrayList<Post> getAllShopsPostList(){
        return this.allShopsPostList;
    }
}
