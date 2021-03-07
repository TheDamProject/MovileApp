package com.jotamarti.golocal.Models;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public abstract class User implements Serializable {

    protected URL avatar;
    protected String email;
    protected String userId;

    public User(URL avatar, String email, String userId) {
        this.avatar = avatar;
        this.email = email;
        this.userId = userId;
    }

    public User() {

    }

    public URL getAvatar() {
        return avatar;
    }

    public void setAvatar(URL avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
