package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.dummy.PostsDummy;
import com.jotamarti.golocal.dummy.ShopsDummy;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private User user;
    public LatLng userCoordinates;
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<List<Shop>> shopsList = new MutableLiveData<>();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    //TODO: Poner lista de posts
    public void setPosts() {
        List<Post> posts = PostsDummy.getITems();
        this.posts.setValue(posts);
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

    public void setShops() {
        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                List<Shop> shops = ShopsDummy.getITems();
                shopsList.postValue(shops);
            }
        }).start();
    }
}
