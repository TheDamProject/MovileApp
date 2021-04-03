package com.jotamarti.golocal.Models;

import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;

public abstract class User implements Parcelable {

    protected String avatar;
    protected String userUid;

    public User(String avatar, String userUid) {
        this.avatar = avatar;
        this.userUid = userUid;
    }

    public User() {

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

}
